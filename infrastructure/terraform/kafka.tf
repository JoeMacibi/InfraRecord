resource "kubernetes_deployment" "kafka" {
  metadata {
    name      = "kafka"
    namespace = kubernetes_namespace.infrarecord.metadata[0].name
    labels = {
      app = "kafka"
    }
  }

  spec {
    replicas = 1
    selector {
      match_labels = {
        app = "kafka"
      }
    }
    template {
      metadata {
        labels = {
          app = "kafka"
        }
      }
      spec {
        container {
          name  = "kafka"
          image = "confluentinc/cp-kafka:7.6.0"
          port {
            container_port = 9092
          }
          port {
            container_port = 29092
          }
          env {
            name  = "KAFKA_BROKER_ID"
            value = "1"
          }
          env {
            name  = "KAFKA_ZOOKEEPER_CONNECT"
            value = "zookeeper:2181"
          }
          env {
            name  = "KAFKA_ADVERTISED_LISTENERS"
            value = "PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092"
          }
          env {
            name  = "KAFKA_LISTENER_SECURITY_PROTOCOL_MAP"
            value = "PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT"
          }
          env {
            name  = "KAFKA_INTER_BROKER_LISTENER_NAME"
            value = "PLAINTEXT"
          }
          env {
            name  = "KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR"
            value = "1"
          }
          resources {
            requests = {
              memory = "512Mi"
              cpu    = "500m"
            }
            limits = {
              memory = "1Gi"
              cpu    = "1000m"
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_deployment" "zookeeper" {
  metadata {
    name      = "zookeeper"
    namespace = kubernetes_namespace.infrarecord.metadata[0].name
    labels = {
      app = "zookeeper"
    }
  }

  spec {
    replicas = 1
    selector {
      match_labels = {
        app = "zookeeper"
      }
    }
    template {
      metadata {
        labels = {
          app = "zookeeper"
        }
      }
      spec {
        container {
          name  = "zookeeper"
          image = "confluentinc/cp-zookeeper:7.6.0"
          port {
            container_port = 2181
          }
          env {
            name  = "ZOOKEEPER_CLIENT_PORT"
            value = "2181"
          }
          env {
            name  = "ZOOKEEPER_TICK_TIME"
            value = "2000"
          }
          resources {
            requests = {
              memory = "256Mi"
              cpu    = "250m"
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "kafka" {
  metadata {
    name      = "kafka"
    namespace = kubernetes_namespace.infrarecord.metadata[0].name
  }
  spec {
    selector = {
      app = "kafka"
    }
    port {
      name        = "broker"
      port        = 9092
      target_port = 9092
    }
    port {
      name        = "internal"
      port        = 29092
      target_port = 29092
    }
    type = "ClusterIP"
  }
}

resource "kubernetes_service" "zookeeper" {
  metadata {
    name      = "zookeeper"
    namespace = kubernetes_namespace.infrarecord.metadata[0].name
  }
  spec {
    selector = {
      app = "zookeeper"
    }
    port {
      port        = 2181
      target_port = 2181
    }
    type = "ClusterIP"
  }
}
