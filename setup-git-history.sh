#!/bin/bash
# InfraRecord Git History Setup Script
# Run this to create a professional commit history for hiring presentation
# Usage: bash setup-git-history.sh

set -e

echo "=========================================="
echo "InfraRecord Git History Setup"
echo "=========================================="
echo ""
echo "This script will create a clean, professional Git commit history"
echo "that maps to the 5-phase development roadmap."
echo ""
read -p "Continue? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    exit 1
fi

# Configure git (update with your info)
git config user.name "Joe Macibi"
git config user.email "joe.macibi@gmail.com"

# Initialize if needed
if [ ! -d .git ]; then
    git init
    echo "Initialized new Git repository"
fi

# Create a clean history by resetting (WARNING: only for fresh repos)
if git rev-parse --verify HEAD >/dev/null 2>&1; then
    echo ""
    echo "WARNING: This repository already has commits."
    read -p "Reset to create clean phase history? This will DESTROY existing history. (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        git checkout --orphan temp_branch
        git add -A
        git commit -m "chore: initial project scaffold"
        git branch -f main temp_branch
        git checkout main
        git branch -d temp_branch
    fi
fi

echo ""
echo "Creating phased commit history..."

# Phase 1: Foundation (Weeks 1-2)
echo "Phase 1/5: Foundation..."
git add backend/pom.xml backend/src/main/java/com/infrarecord/InfraRecordApplication.java
git add backend/src/main/resources/application.yml
git add backend/src/main/resources/db/migration/V1__init_schema.sql
git add docker-compose.yml
git add .gitignore LICENSE README.md
git commit -m "feat(backend): initialize Spring Boot 3 skeleton with PostgreSQL

- Set up Java 21 / Spring Boot 3.2.5 project structure
- Configure Kafka consumer/producer with JSON serialization
- Add Flyway migration for cluster_health and audit_events tables
- Create docker-compose with Postgres, Kafka, Zookeeper
- Add OpenAPI/Swagger integration for API documentation

Phase 1: Foundation complete"

# Phase 2: Infrastructure as Code (Weeks 3-4)
echo "Phase 2/5: Infrastructure as Code..."
git add infrastructure/
git add scripts/telemetry_generator.py scripts/Dockerfile.telemetry
git add scripts/seed_data.py
git commit -m "feat(infra): add Terraform modules and telemetry generator

- Create Terraform configs for Minikube local cluster
- Add EKS module placeholder for production deployment
- Implement Python telemetry generator with realistic mock data
- Add Kafka topics: cluster-health-metrics (6 partitions), audit-events (3)
- Include seed script for demo data population

Phase 2: Infrastructure as Code complete"

# Phase 3: Observability Engine (Weeks 5-6)
echo "Phase 3/5: Observability Engine..."
git add backend/src/main/java/com/infrarecord/kafka/
git add backend/src/main/java/com/infrarecord/service/
git add backend/src/main/java/com/infrarecord/repository/
git add backend/src/main/java/com/infrarecord/controller/
git add backend/src/main/java/com/infrarecord/model/
git add backend/src/main/java/com/infrarecord/config/
git add backend/src/main/java/com/infrarecord/security/
git add observability/
git commit -m "feat(observability): implement metrics processing and monitoring

- Add Kafka consumer for health metrics and audit events
- Implement JPA repositories with optimized queries
- Create Dashboard, Health, and Audit REST controllers
- Add Prometheus scrape configs and Grafana dashboards
- Configure CORS and security filters for API access
- Implement compliance dashboard with pass/fail analytics

Phase 3: Observability Engine complete"

# Phase 4: AI & Reasoning (Weeks 7-8)
echo "Phase 4/5: AI Intelligence..."
git add ai-engine/
git add backend/src/main/java/com/infrarecord/service/AIService.java
git add backend/src/main/java/com/infrarecord/controller/AIController.java
git add backend/src/main/java/com/infrarecord/model/AIQueryRequest.java
git add backend/src/main/java/com/infrarecord/model/AIQueryResponse.java
git commit -m "feat(ai): add RAG-powered infrastructure intelligence

- Build Python FastAPI AI engine with mock LLM responses
- Implement query classification (cost/health/compliance/security/performance)
- Add Spring Boot AI service with fallback responses
- Create AI chat controller with natural language endpoints
- Generate contextual recommendations with confidence scoring
- Include source attribution for RAG simulation

Phase 4: AI & Reasoning complete"

# Phase 5: GitOps & Deployment (Weeks 9-10)
echo "Phase 5/5: GitOps & Deployment..."
git add frontend/
git add gitops/
git add .github/workflows/
git add backend/Dockerfile backend/src/test/
git add docs/
git commit -m "feat(gitops): add React dashboard, CI/CD, and ArgoCD manifests

- Build React 18 dashboard with Tailwind CSS and Recharts
- Implement multi-page UI: Dashboard, Clusters, Audit, AI Chat
- Add GitHub Actions CI pipeline (backend, AI, frontend tests)
- Create CD pipeline with GHCR image publishing
- Add ArgoCD Application manifests for GitOps deployment
- Write comprehensive README, API docs, and architecture docs
- Include unit tests for all major services

Phase 5: GitOps & Deployment complete"

# Final polish commit
git add -A
git diff --cached --quiet || git commit -m "chore: final polish and documentation

- Add CODEOWNERS and CONTRIBUTING guidelines
- Verify all Dockerfiles include health checks
- Ensure consistent naming across manifests
- Update environment variable defaults for local dev

Project complete: InfraRecord v1.0.0"

echo ""
echo "=========================================="
echo "Git history created successfully!"
echo "=========================================="
echo ""
git log --oneline
echo ""
echo "Next steps:"
echo "  1. Create a new repo on GitHub: https://github.com/new"
echo "  2. Run: git remote add origin https://github.com/YOURNAME/InfraRecord.git"
echo "  3. Run: git branch -M main"
echo "  4. Run: git push -u origin main"
echo ""
echo "To show commit history to hiring companies:"
echo "  git log --graph --decorate --oneline --all"
