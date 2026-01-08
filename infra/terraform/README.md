Terraform module to create GCP resources for GitHub Actions OIDC and Artifact Registry.

Usage
------

1. Install Terraform v1.1+
2. Authenticate with GCP: `gcloud auth application-default login` or configure a service account for Terraform.
3. Initialize and apply:

```powershell
cd infra/terraform
terraform init
terraform apply -var="project_id=your-gcp-project-id" -var="github_repository=yuktaakhade/movieworld-argocd"
```

This creates:
- A service account `movieworld-service-account` (by default)
- An Artifact Registry Docker repository
- A workload identity pool and provider for GitHub OIDC
- IAM binding that allows the specified GitHub repo to impersonate the service account

After apply
-----------
- Use the `service_account_email` output in your GitHub Actions `google-github-actions/auth@v2` step as `service_account:` value.
- If you prefer finer scoping, adjust roles in `main.tf`.
