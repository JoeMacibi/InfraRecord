# Minikube-specific configurations
# This module sets up local Minikube cluster prerequisites

resource "local_file" "minikube_start" {
  filename = "${path.module}/scripts/start-minikube.sh"
  content  = <<-EOF
    #!/bin/bash
    set -e

    echo "Starting Minikube cluster for InfraRecord..."
    minikube start --driver=docker --cpus=4 --memory=8192 --kubernetes-version=v1.29.0

    echo "Enabling required addons..."
    minikube addons enable ingress
    minikube addons enable metrics-server

    echo "Minikube cluster ready!"
    echo "Run: kubectl config use-context minikube"
  EOF
  file_permission = "0755"
}

resource "local_file" "minikube_stop" {
  filename = "${path.module}/scripts/stop-minikube.sh"
  content  = <<-EOF
    #!/bin/bash
    echo "Stopping Minikube cluster..."
    minikube stop
    echo "Minikube stopped."
  EOF
  file_permission = "0755"
}
