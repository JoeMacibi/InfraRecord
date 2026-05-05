resource "kubernetes_namespace" "infrarecord" {
  metadata {
    name = var.namespace
    labels = {
      environment = var.environment
      managed_by  = "terraform"
    }
  }
}
