variable "project_id" {
  type = string
}

variable "region" {
  type    = string
  default = "us-central1"
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
