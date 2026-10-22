provider "aws" {
  region = var.aws_region
}

resource "aws_security_group" "eks_cluster_sg" {
  name        = "eks-cluster-sg-${var.cluster_name}"
  description = "Security group for EKS cluster ${var.cluster_name}"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = 8082
    to_port     = 8082
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 30000
    to_port     = 30000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "eks-cluster-sg-${var.cluster_name}"
  }
}

resource "aws_eks_cluster" "my_cluster" {
  name     = var.cluster_name
  role_arn = var.role_arn
  version  = var.kubernetes_version

  vpc_config {
    subnet_ids         = var.subnet_ids
    security_group_ids = [aws_security_group.eks_cluster_sg.id]
  }
}

resource "aws_eks_node_group" "my_node_group" {
  cluster_name    = aws_eks_cluster.my_cluster.name
  node_group_name = "noeud1"
  node_role_arn   = var.role_arn
  subnet_ids      = var.subnet_ids
  instance_types  = ["t3.medium"]

  scaling_config {
    desired_size = 2
    max_size     = 3
    min_size     = 1
  }
}

# Ouvre le NodePort 30000 sur le groupe de securite cree par EKS
# (c'est celui que les noeuds utilisent reellement)
resource "aws_security_group_rule" "nodeport_30000" {
  type              = "ingress"
  from_port         = 30000
  to_port           = 30000
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_eks_cluster.my_cluster.vpc_config[0].cluster_security_group_id
}

# DEVELOP VERSION - preserved as comments
# data "aws_iam_role" "lab_role" {
#   name = "LabRole"
# }
#
# data "aws_vpc" "default" {
#   default = true
# }
#
# data "aws_subnets" "default" {
#   filter {
#     name   = "vpc-id"
#     values = [data.aws_vpc.default.id]
#   }
#   filter {
#     name   = "availability-zone"
#     values = ["us-east-1a", "us-east-1b", "us-east-1c"]
#   }
# }
#
# resource "aws_eks_cluster" "this" {
#   name     = var.cluster_name
#   role_arn = data.aws_iam_role.lab_role.arn
#
#   vpc_config {
#     subnet_ids = data.aws_subnets.default.ids
#   }
# }
#
# resource "aws_eks_node_group" "nodes" {
#   cluster_name    = aws_eks_cluster.this.name
#   node_group_name = "${var.cluster_name}-nodes"
#   node_role_arn   = data.aws_iam_role.lab_role.arn
#   subnet_ids      = data.aws_subnets.default.ids
#   instance_types  = ["t3.medium"]
#
#   scaling_config {
#     desired_size = 2
#     min_size     = 1
#     max_size     = 2
#   }
# }
#
# resource "aws_security_group_rule" "nodeport" {
#   type              = "ingress"
#   from_port         = 30000
#   to_port           = 30000
#   protocol          = "tcp"
#   cidr_blocks       = ["0.0.0.0/0"]
#   security_group_id = aws_eks_cluster.this.vpc_config[0].cluster_security_group_id
# }
