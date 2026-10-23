variable "aws_region" {
  description = "La region AWS"
  type        = string
  default     = "us-east-1"
}

variable "cluster_name" {
  description = "Nom du cluster EKS"
  type        = string
  default     = "mykubernetes"
}

variable "kubernetes_version" {
  description = "Version de Kubernetes"
  type        = string
  default     = "1.34"
}

variable "role_arn" {
  description = "ARN du role IAM (LabRole) utilise par EKS et les noeuds"
  type        = string
}

variable "vpc_id" {
  description = "ID du VPC"
  type        = string
}

variable "subnet_ids" {
  description = "IDs des sous-reseaux (2 zones differentes)"
  type        = list(string)
}

# DEVELOP VERSION - preserved as comments
# variable "region" {
#   default = "us-east-1"
# }
#
# variable "cluster_name" {
#   default = "mykubernetes"
# }
