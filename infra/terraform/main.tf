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
