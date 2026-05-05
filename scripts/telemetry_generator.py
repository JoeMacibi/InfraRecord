#!/usr/bin/env python3
"""
InfraRecord Mock Telemetry Generator
Generates realistic infrastructure metrics and pushes to Kafka.
"""

import json
import time
import random
import os
from datetime import datetime, timezone
from kafka import KafkaProducer

KAFKA_BOOTSTRAP = os.getenv('KAFKA_BOOTSTRAP', 'localhost:9092')
INTERVAL = int(os.getenv('INTERVAL_SECONDS', '10'))

CLUSTERS = [
    {"id": "ke-health-eks-001", "nodes": ["ip-10-0-1-45", "ip-10-0-1-46", "ip-10-0-1-47"], "type": "eks"},
    {"id": "on-prem-k8s-002", "nodes": ["srv-k8s-01", "srv-k8s-02", "srv-k8s-03"], "type": "on-prem"},
    {"id": "dev-minikube-003", "nodes": ["minikube"], "type": "minikube"},
]

USERS = ["j.macibi", "s.chen", "a.patel", "m.rodriguez", "system"]
ACTIONS = ["TERRAFORM_APPLY", "TERRAFORM_DESTROY", "KUBECTL_APPLY", "KUBECTL_DELETE", "MANUAL_EDIT", "SECRET_ROTATION"]
RESOURCES = ["eks_node_group", "deployment", "service", "configmap", "secret", "ingress", "iam_policy"]
COMPLIANCE_STATUSES = ["PASSED", "PASSED", "PASSED", "PASSED", "FAILED", "PENDING_REVIEW"]


def generate_health_packet(cluster):
    node = random.choice(cluster["nodes"])
    cpu = round(random.uniform(30, 95), 1)
    mem = round(random.uniform(40, 92), 1)

    if cpu > 85 or mem > 88:
        status = "CRITICAL"
    elif cpu > 70 or mem > 75:
        status = "WARNING"
    else:
        status = "HEALTHY"

    return {
        "cluster_id": cluster["id"],
        "node_id": node,
        "timestamp": datetime.now(timezone.utc).isoformat().replace('+00:00', 'Z'),
        "metrics": {
            "cpu_utilization": cpu,
            "memory_utilization": mem,
            "disk_io": random.choice(["low", "medium", "high"]),
            "network_rx_mbps": round(random.uniform(10, 500), 1),
            "network_tx_mbps": round(random.uniform(10, 500), 1),
        },
        "active_pods": random.randint(3, 25),
        "status": status
    }


def generate_audit_event():
    action = random.choice(ACTIONS)
    status = random.choice(COMPLIANCE_STATUSES)

    change_summaries = {
        "TERRAFORM_APPLY": f"Scaled from {random.randint(2,4)} to {random.randint(4,8)} nodes",
        "TERRAFORM_DESTROY": f"Removed {random.randint(1,3)} deprecated resources",
        "KUBECTL_APPLY": f"Updated image tag to v{random.randint(1,5)}.{random.randint(0,9)}.{random.randint(0,9)}",
        "KUBECTL_DELETE": f"Removed stale pod in namespace production",
        "MANUAL_EDIT": f"Updated configuration via dashboard",
        "SECRET_ROTATION": f"Rotated credentials for {random.choice(['database', 'api-gateway', 'payment-service'])}",
    }

    return {
        "event_id": f"audit-{random.randint(1000, 9999)}",
        "user": random.choice(USERS),
        "action": action,
        "resource": random.choice(RESOURCES),
        "change_summary": change_summaries.get(action, "Infrastructure change executed"),
        "compliance_status": status,
        "cluster_id": random.choice([c["id"] for c in CLUSTERS]),
        "timestamp": datetime.now(timezone.utc).isoformat().replace('+00:00', 'Z')
    }


def main():
    print(f"Connecting to Kafka at {KAFKA_BOOTSTRAP}...")
    producer = KafkaProducer(
        bootstrap_servers=KAFKA_BOOTSTRAP,
        value_serializer=lambda v: json.dumps(v).encode('utf-8'),
        key_serializer=lambda v: v.encode('utf-8') if v else None,
        retries=5,
        retry_backoff_ms=1000,
    )

    print(f"Telemetry generator started. Interval: {INTERVAL}s")
    print("Press Ctrl+C to stop.
")

    try:
        while True:
            # Generate health metrics
            for cluster in CLUSTERS:
                packet = generate_health_packet(cluster)
                producer.send(
                    'cluster-health-metrics',
                    key=cluster["id"],
                    value=packet
                )
                print(f"[HEALTH] {cluster['id']}: {packet['status']} | CPU: {packet['metrics']['cpu_utilization']}% | Pods: {packet['active_pods']}")

            # Generate audit event (30% chance per cycle)
            if random.random() < 0.3:
                event = generate_audit_event()
                producer.send(
                    'infrastructure-audit-events',
                    key=event["event_id"],
                    value=event
                )
                print(f"[AUDIT] {event['event_id']}: {event['action']} by {event['user']} -> {event['compliance_status']}")

            producer.flush()
            time.sleep(INTERVAL)

    except KeyboardInterrupt:
        print("\nShutting down telemetry generator...")
    finally:
        producer.close()


if __name__ == "__main__":
    main()
