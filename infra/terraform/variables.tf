variable "project_id" {
  type = string
}

variable "region" {
  type    = string
  default = "us-west1"
}

variable "service_account_id" {
  type    = string
  default = "movieworld-service-account"
}

variable "artifact_repo_id" {
  type    = string
  default = "movieworld-dockerimage-repo"
}

variable "workload_pool_id" {
  type    = string
  default = "wif-pool"
}

variable "workload_provider_id" {
  type    = string
  default = "wif-provider"
}

variable "github_repository" {
  type = string
  description = "The GitHub repository in the form 'owner/repo' that will be allowed to impersonate the service account"
}

variable "network_name" {
  type    = string
  default = "movieworld-vpc"
}

variable "subnetwork_name" {
  type    = string
  default = "movieworld-subnet"
}

variable "subnet_cidr" {
  type    = string
  default = "10.0.0.0/24"
}

variable "gke_cluster_name" {
  type    = string
  default = "app-cluster"
}

variable "gke_location" {
  type    = string
  default = "us-west1-a"
}

variable "node_machine_type" {
  type    = string
  default = "e2-micro"
}

variable "node_count" {
  type    = number
  default = 1
}
