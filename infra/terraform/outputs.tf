output "workload_identity_pool_name" {
  value = google_iam_workload_identity_pool.github_pool.name
}

output "workload_identity_provider_name" {
  value = google_iam_workload_identity_pool_provider.github_provider.name
}

output "service_account_email" {
  value = google_service_account.github_actions_sa.email
}
