terraform {
  required_version = ">= 1.1"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 4.0"
    }
  }
}

provider "google" {
  project = var.project_id
  region  = var.region
}

# Enable required Google APIs
resource "google_project_service" "artifact_registry_api" {
  service = "artifactregistry.googleapis.com"
  project = var.project_id
}

resource "google_project_service" "container_api" {
  service = "container.googleapis.com"
  project = var.project_id
}

resource "google_project_service" "iamcredentials_api" {
  service = "iamcredentials.googleapis.com"
  project = var.project_id
}

# Create a service account for GitHub Actions
resource "google_service_account" "github_actions_sa" {
  account_id   = var.service_account_id
  display_name = "GitHub Actions Service Account for MovieWorld CI"
}

# Create Artifact Registry repository
resource "google_artifact_registry_repository" "repo" {
  provider = google
  location = var.region
  repository_id = var.artifact_repo_id
  format = "DOCKER"
  description = "Artifact Registry repo for MovieWorld Docker images"
}

# Create Workload Identity Pool
resource "google_iam_workload_identity_pool" "github_pool" {
  provider = google
  workload_identity_pool_id = var.workload_pool_id
  display_name = "GitHub Workload Identity Pool"
  description = "Pool used to trust GitHub Actions OIDC tokens"
}

# Create Workload Identity Provider for the pool
resource "google_iam_workload_identity_pool_provider" "github_provider" {
  provider = google
  workload_identity_pool_id = google_iam_workload_identity_pool.github_pool.workload_identity_pool_id
  workload_identity_pool_provider_id = var.workload_provider_id

  display_name = "GitHub Provider"
  oidc {
    issuer_uri = "https://token.actions.githubusercontent.com"
  }

  attribute_mapping = {
    "google.subject" = "assertion.sub"
    "attribute.actor" = "assertion.actor"
    "attribute.repository" = "assertion.repository"
  }
}

# Allow GitHub principal to impersonate the service account (workload identity user)
resource "google_service_account_iam_binding" "sa_wif_binding" {
  service_account_id = google_service_account.github_actions_sa.name
  role = "roles/iam.workloadIdentityUser"
  members = [
    "principalSet://iam.googleapis.com/${google_iam_workload_identity_pool.github_pool.name}/attribute.repository/${var.github_repository}"
  ]
}

# Grant Artifact Registry writer to the service account
resource "google_project_iam_member" "artifact_writer" {
  project = var.project_id
  role    = "roles/artifactregistry.writer"
  member  = "serviceAccount:${google_service_account.github_actions_sa.email}"
}

output "service_account_email" {
  value = google_service_account.github_actions_sa.email
}

output "artifact_registry_repo" {
  value = google_artifact_registry_repository.repo.name
}

# --------------------
# Networking & GKE
# --------------------

resource "google_compute_network" "vpc" {
  name                    = var.network_name
  auto_create_subnetworks = false
}

resource "google_compute_subnetwork" "subnet" {
  name          = var.subnetwork_name
  ip_cidr_range = var.subnet_cidr
  region        = var.region
  network       = google_compute_network.vpc.id
}

resource "google_container_cluster" "primary" {
  name     = var.gke_cluster_name
  location = var.gke_location

  networking_mode = "VPC_NATIVE"
  network         = google_compute_network.vpc.id
  subnetwork      = google_compute_subnetwork.subnet.id

  initial_node_count = 1

  workload_identity_config {
    workload_pool = "${var.project_id}.svc.id.goog"
  }

  remove_default_node_pool = true
  min_master_version = "latest"

  ip_allocation_policy {
  }

  depends_on = [
    google_project_service.container_api,
    google_project_service.artifact_registry_api
  ]
}

resource "google_container_node_pool" "primary_nodes" {
  name       = "primary-node-pool"
  cluster    = google_container_cluster.primary.name
  location   = var.gke_location

  node_config {
    machine_type = var.node_machine_type
    oauth_scopes = [
      "https://www.googleapis.com/auth/cloud-platform",
    ]
    service_account = google_service_account.github_actions_sa.email
  }

  initial_node_count = var.node_count
}
