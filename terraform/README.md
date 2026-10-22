# Terraform state

Terraform stores the EKS state in the private, versioned S3 bucket
`gl5-g5-foyer-tfstate-479685814246-us-east-1`, at
`foyer/terraform.tfstate`. S3 native lock files prevent concurrent Jenkins
builds from changing the state at the same time. The backend requires Terraform
1.10 or newer.

The bucket is intentionally managed separately from this Terraform
configuration: deleting it would remove the state needed to manage the EKS
cluster. Keep bucket versioning, encryption, and S3 public-access blocking
enabled. Do not commit local Terraform state files.

Jenkins initializes this backend and imports the existing security group,
cluster, or node group when they are present in AWS but absent from the shared
state. Review the Terraform plan in the build log before applying it. The
`DESTROY_INFRA` Jenkins parameter intentionally destroys managed infrastructure;
do not enable it during a deployment.

For local work, use AWS credentials with access to the same S3 state bucket and
run `terraform init -reconfigure` from this directory. Do not use a separate
state file to apply changes to this cluster.

An AWS learner-lab account may be reset or expire, so resources and the state
bucket are only persistent for as long as that account remains available.
