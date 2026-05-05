#!/usr/bin/env python3
"""
Seed script to populate initial data for demo purposes.
Run after services are up: python seed_data.py
"""

import requests
import json
from datetime import datetime, timezone

BASE_URL = "http://localhost:8080/api/v1"


def seed_health_data():
    print("Seeding health metrics...")
    clusters = [
        {"id": "ke-health-eks-001", "node": "ip-10-0-1-45", "cpu": 74.5, "mem": 82.1, "pods": 12, "status": "HEALTHY"},
        {"id": "ke-health-eks-001", "node": "ip-10-0-1-46", "cpu": 62.3, "mem": 71.4, "pods": 8, "status": "HEALTHY"},
        {"id": "on-prem-k8s-002", "node": "srv-k8s-01", "cpu": 45.2, "mem": 58.9, "pods": 15, "status": "HEALTHY"},
        {"id": "dev-minikube-003", "node": "minikube", "cpu": 91.2, "mem": 88.5, "pods": 6, "status": "WARNING"},
    ]

    for c in clusters:
        payload = {
            "cluster_id": c["id"],
            "node_id": c["node"],
            "timestamp": datetime.now(timezone.utc).isoformat().replace('+00:00', 'Z'),
            "metrics": {
                "cpu_utilization": c["cpu"],
                "memory_utilization": c["mem"],
                "disk_io": "low"
            },
            "active_pods": c["pods"],
            "status": c["status"]
        }
        try:
            r = requests.post(f"{BASE_URL}/health/ingest", json=payload, timeout=5)
            print(f"  {c['id']}/{c['node']}: {r.status_code}")
        except Exception as e:
            print(f"  Error seeding {c['id']}: {e}")


def seed_audit_data():
    print("\nSeeding audit events...")
    events = [
        {"event_id": "audit-9982", "user": "j.macibi", "action": "TERRAFORM_APPLY", "resource": "eks_node_group", 
         "change_summary": "Scaled from 3 to 5 nodes", "compliance_status": "PASSED", "cluster_id": "ke-health-eks-001"},
        {"event_id": "audit-9983", "user": "s.chen", "action": "KUBECTL_APPLY", "resource": "deployment", 
         "change_summary": "Updated image tag to v2.1.0", "compliance_status": "PASSED", "cluster_id": "ke-health-eks-001"},
        {"event_id": "audit-9984", "user": "a.patel", "action": "MANUAL_EDIT", "resource": "configmap", 
         "change_summary": "Updated environment variables", "compliance_status": "PENDING_REVIEW", "cluster_id": "on-prem-k8s-002"},
        {"event_id": "audit-9985", "user": "system", "action": "SECRET_ROTATION", "resource": "secret", 
         "change_summary": "Rotated database credentials", "compliance_status": "PASSED", "cluster_id": "ke-health-eks-001"},
        {"event_id": "audit-9986", "user": "m.rodriguez", "action": "TERRAFORM_APPLY", "resource": "iam_policy", 
         "change_summary": "Added S3 read permissions", "compliance_status": "FAILED", "cluster_id": "on-prem-k8s-002"},
    ]

    for e in events:
        e["timestamp"] = datetime.now(timezone.utc).isoformat().replace('+00:00', 'Z')
        try:
            r = requests.post(f"{BASE_URL}/audit/events", json=e, timeout=5)
            print(f"  {e['event_id']}: {r.status_code}")
        except Exception as ex:
            print(f"  Error seeding {e['event_id']}: {ex}")


if __name__ == "__main__":
    print("InfraRecord Data Seeder")
    print("=" * 40)
    seed_health_data()
    seed_audit_data()
    print("\nDone! Check the dashboard at http://localhost")
