variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "cluster_name" {
  description = "Name of the EKS cluster (must match CLUSTER_NAME in the Jenkinsfile)"
  type        = string
  default     = "mykubernetes"
}
