# AWS EKS module placeholder
# Replace with actual EKS configuration for production deployment

variable "cluster_name" {
  description = "EKS cluster name"
  type        = string
  default     = "infrarecord-eks"
}

variable "region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

# Outputs for ArgoCD integration
output "cluster_endpoint" {
  description = "EKS cluster endpoint"
  value       = "https://placeholder.eks.amazonaws.com"
}

output "cluster_name" {
  description = "EKS cluster name"
  value       = var.cluster_name
}
