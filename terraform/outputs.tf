output "cluster_endpoint" {
  description = "L'endpoint du cluster EKS"
  value       = aws_eks_cluster.my_cluster.endpoint
}

output "cluster_name" {
  description = "Le nom du cluster EKS"
  value       = aws_eks_cluster.my_cluster.name
}

output "cluster_role_arn" {
  description = "L'ARN du role IAM du cluster EKS"
  value       = aws_eks_cluster.my_cluster.role_arn
}

# DEVELOP VERSION - preserved as comments
# output "cluster_name" {
#   value = aws_eks_cluster.this.name
# }
#
# output "cluster_endpoint" {
#   value = aws_eks_cluster.this.endpoint
# }
