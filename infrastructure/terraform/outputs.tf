output "namespace" {
  description = "Created Kubernetes namespace"
  value       = kubernetes_namespace.infrarecord.metadata[0].name
}

output "postgres_service" {
  description = "PostgreSQL service endpoint"
  value       = "${kubernetes_service.postgres.metadata[0].name}.${kubernetes_namespace.infrarecord.metadata[0].name}.svc.cluster.local"
}

output "kafka_bootstrap" {
  description = "Kafka bootstrap servers"
  value       = "${kubernetes_service.kafka.metadata[0].name}.${kubernetes_namespace.infrarecord.metadata[0].name}.svc.cluster.local:9092"
}
