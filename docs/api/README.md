# InfraRecord API Documentation

## Base URL
```
http://localhost:8080/api/v1
```

## Authentication
Currently uses basic CORS for development. Production deployments should integrate OAuth2/JWT.

## Endpoints

### Dashboard

#### GET /dashboard/summary
Returns overall cluster health summary.

**Response:**
```json
{
  "clusters": [
    {
      "clusterId": "ke-health-eks-001",
      "status": "HEALTHY",
      "lastUpdated": "2026-05-04T12:00:00Z",
      "activePods": 12,
      "metrics": {
        "cpu_utilization": "74.5",
        "memory_utilization": "82.1"
      }
    }
  ],
  "totalClusters": 3,
  "criticalCount": 0,
  "timestamp": "2026-05-04T12:00:00Z"
}
```

#### GET /dashboard/compliance
Returns compliance dashboard metrics.

**Response:**
```json
{
  "passed": 142,
  "failed": 3,
  "pendingReview": 2,
  "total": 147,
  "passRate": 96.6,
  "period": "Last 30 days",
  "recentFailures": [...]
}
```

### Health Metrics

#### POST /health/ingest
Ingest health metrics packet.

**Request Body:**
```json
{
  "cluster_id": "ke-health-eks-001",
  "node_id": "ip-10-0-1-45",
  "timestamp": "2026-05-04T12:00:00Z",
  "metrics": {
    "cpu_utilization": 74.5,
    "memory_utilization": 82.1,
    "disk_io": "low"
  },
  "active_pods": 12,
  "status": "Healthy"
}
```

### Audit

#### POST /audit/events
Record a new audit event.

**Request Body:**
```json
{
  "event_id": "audit-9982",
  "user": "j.macibi",
  "action": "TERRAFORM_APPLY",
  "resource": "eks_node_group",
  "change_summary": "Scaled from 3 to 5 nodes",
  "compliance_status": "PASSED",
  "timestamp": "2026-05-04T12:05:00Z"
}
```

### AI Engine

#### POST /ai/query
Query infrastructure with natural language.

**Request Body:**
```json
{
  "query": "What is the cost optimization opportunity for eks-001?",
  "clusterId": "ke-health-eks-001",
  "context": "monthly-review"
}
```

**Response:**
```json
{
  "response": "Based on historical metrics, cluster ke-health-eks-001 shows 74.5% average CPU utilization...",
  "sources": [
    {"source": "Prometheus", "relevance": 0.92},
    {"source": "AWS Cost Explorer", "relevance": 0.88}
  ],
  "confidence": "HIGH",
  "recommendation": "DOWNSIZE"
}
```
