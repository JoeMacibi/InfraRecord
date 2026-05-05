# InfraRecord

> **Unified Infrastructure Observability & Governance Platform**

[![CI](https://github.com/joemacibi/InfraRecord/actions/workflows/ci.yml/badge.svg)](https://github.com/joemacibi/InfraRecord/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

InfraRecord is a production-grade platform for monitoring, auditing, and optimizing hybrid cloud infrastructure (AWS EKS + On-Premises). It combines real-time metrics ingestion, automated CISA-aligned compliance auditing, and AI-driven cost/performance recommendations.

## Architecture

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   React     │────▶│ Spring Boot │────▶│  PostgreSQL │
│  Dashboard  │◀────│   Backend   │◀────│  (Audit)    │
└─────────────┘     └──────┬──────┘     └─────────────┘
                           │
                    ┌──────┴──────┐
                    │    Kafka    │
                    │  (Metrics)  │
                    └──────┬──────┘
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
        ┌─────────┐  ┌─────────┐  ┌─────────┐
        │Telemetry│  │Prometheus│  │  AI     │
        │Generator│  │Grafana  │  │ Engine  │
        └─────────┘  └─────────┘  └─────────┘
```

## Quick Start

### Prerequisites
- Docker & Docker Compose
- JDK 21 (for local backend development)
- Node.js 20 (for local frontend development)
- Python 3.12 (for AI engine development)

### 1. Clone & Start Services

```bash
git clone https://github.com/joemacibi/InfraRecord.git
cd InfraRecord

# Start all infrastructure and services
docker-compose up -d

# Wait for services to be healthy (~60s)
docker-compose ps
```

### 2. Seed Demo Data

```bash
# Install requests if needed
pip install requests

# Seed initial health metrics and audit events
python scripts/seed_data.py
```

### 3. Access the Platform

| Service | URL | Credentials |
|---------|-----|-------------|
| Dashboard | http://localhost | — |
| API Docs | http://localhost:8080/swagger-ui.html | — |
| Prometheus | http://localhost:9090 | — |
| Grafana | http://localhost:3000 | admin/admin |

### 4. Generate Live Telemetry

```bash
# Start the mock telemetry generator
docker-compose --profile demo up -d telemetry-generator
```

## Development

### Backend (Spring Boot)
```bash
cd backend
mvn spring-boot:run
```

### AI Engine (FastAPI)
```bash
cd ai-engine
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

### Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
```

## API Endpoints

### Dashboard
- `GET /api/v1/dashboard/summary` — Cluster health summary
- `GET /api/v1/dashboard/compliance` — Compliance metrics
- `GET /api/v1/dashboard/alerts` — Active alerts

### Health Metrics
- `POST /api/v1/health/ingest` — Ingest metrics
- `GET /api/v1/health/clusters/{id}` — Cluster health history
- `GET /api/v1/health/critical` — Critical events

### Audit
- `POST /api/v1/audit/events` — Record audit event
- `GET /api/v1/audit/dashboard` — Compliance dashboard
- `GET /api/v1/audit/failed` — Failed compliance events

### AI Engine
- `POST /api/v1/ai/query` — Natural language infrastructure query
- `GET /api/v1/ai/status` — AI engine status

Full API documentation available at `/swagger-ui.html` when backend is running.

## Deployment

### Local Kubernetes (Minikube)
```bash
# Start Minikube
minikube start --cpus=4 --memory=8192

# Apply Terraform
terraform -chdir=infrastructure/terraform init
terraform -chdir=infrastructure/terraform apply

# Install ArgoCD
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# Apply InfraRecord apps
kubectl apply -f gitops/argocd/
```

### Production (EKS)
See `infrastructure/terraform/modules/eks/` for EKS-specific configurations.

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Backend API | Java 21 / Spring Boot 3.x |
| Automation | Terraform |
| Orchestration | Kubernetes (EKS & Minikube) |
| Messaging | Apache Kafka |
| Databases | PostgreSQL (Audit), Prometheus (Time-series) |
| AI Intelligence | Python (FastAPI), LangChain pattern |
| Frontend | React 18, TypeScript, Tailwind CSS |
| CI/CD | GitHub Actions & ArgoCD |

## Compliance

InfraRecord implements automated audit trails aligned with **CISA** standards:
- Every infrastructure change is captured and immutable
- Compliance scoring with pass/fail/pending states
- 30-day rolling audit history with breakdown analytics

## License

MIT License — see [LICENSE](LICENSE) for details.

---

Built with precision for 99.9% uptime environments.
