from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Dict, Optional
import random

app = FastAPI(
    title="InfraRecord AI Engine",
    description="RAG-powered infrastructure intelligence and optimization",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

class AIQueryRequest(BaseModel):
    query: str
    clusterId: Optional[str] = None
    context: Optional[str] = None

class Source(BaseModel):
    source: str
    relevance: float

class AIQueryResponse(BaseModel):
    response: str
    sources: List[Source]
    confidence: str
    recommendation: str

class HealthStatus(BaseModel):
    status: str
    model: str
    version: str
    capabilities: List[str]

# Mock knowledge base for RAG simulation
KNOWLEDGE_BASE = {
    "cost": {
        "response": "Based on historical metrics, cluster {cluster} shows {cpu}% average CPU utilization with memory at {mem}%. Recommend reducing node count from {current} to {recommended} during off-peak hours (00:00-06:00 UTC) to save approximately ${savings}/month.",
        "recommendation": "DOWNSIZE",
        "sources": [
            {"source": "Prometheus", "relevance": 0.92},
            {"source": "AWS Cost Explorer", "relevance": 0.88}
        ]
    },
    "health": {
        "response": "All monitored clusters are currently {status}. No critical alerts in the last 24 hours. Node {node} has elevated memory utilization at {mem}% but remains within acceptable thresholds.",
        "recommendation": "MONITOR",
        "sources": [
            {"source": "Prometheus", "relevance": 0.95},
            {"source": "Cluster Health API", "relevance": 0.90}
        ]
    },
    "compliance": {
        "response": "Compliance status: {rate}% pass rate over the last 30 days. {pending} pending reviews require attention: {issues}.",
        "recommendation": "REVIEW",
        "sources": [
            {"source": "PostgreSQL Audit", "relevance": 0.94},
            {"source": "CISA Framework", "relevance": 0.87}
        ]
    },
    "security": {
        "response": "Security posture: {score}/100. Detected {issues} active vulnerabilities. Secret rotation is {rotation_status} on cluster {cluster}. Recommend immediate review of IAM policies.",
        "recommendation": "REMEDIATE",
        "sources": [
            {"source": "Security Scanner", "relevance": 0.93},
            {"source": "Vault Audit", "relevance": 0.89}
        ]
    },
    "performance": {
        "response": "Performance analysis for {cluster}: P95 latency is {latency}ms, throughput {tps} req/s. Bottleneck identified in pod {pod}. Horizontal scaling recommended to {replicas} replicas.",
        "recommendation": "SCALE",
        "sources": [
            {"source": "Jaeger Traces", "relevance": 0.91},
            {"source": "Prometheus", "relevance": 0.89}
        ]
    }
}

def generate_mock_metrics():
    return {
        "cpu": round(random.uniform(45, 85), 1),
        "mem": round(random.uniform(60, 92), 1),
        "current_nodes": random.randint(3, 8),
        "recommended_nodes": random.randint(2, 5),
        "savings": random.randint(200, 800),
        "status": random.choice(["HEALTHY", "HEALTHY", "HEALTHY", "WARNING"]),
        "node": f"ip-10-0-{random.randint(1,5)}-{random.randint(10,99)}",
        "rate": round(random.uniform(94.0, 99.9), 1),
        "pending": random.randint(0, 5),
        "issues": random.choice([
            "secret rotation overdue on cluster ke-health-eks-001",
            "IAM policy drift detected on on-prem-k8s-002",
            "network policy gap in namespace production",
            "RBAC misconfiguration in staging environment"
        ]),
        "score": random.randint(78, 98),
        "vuln_count": random.randint(0, 12),
        "rotation_status": random.choice(["current", "overdue by 3 days", "overdue by 7 days"]),
        "latency": random.randint(45, 320),
        "tps": random.randint(1200, 8500),
        "pod": f"api-gateway-{random.randint(1,5)}-{random.randint(100,999)}",
        "replicas": random.randint(3, 10)
    }

def classify_query(query: str) -> str:
    q = query.lower()
    if any(w in q for w in ["cost", "money", "spend", "rightsize", "optimize", "billing", "price"]):
        return "cost"
    elif any(w in q for w in ["health", "status", "up", "down", "running", "alive", "node"]):
        return "health"
    elif any(w in q for w in ["compliance", "audit", "cisa", "regulation", "policy", "governance"]):
        return "compliance"
    elif any(w in q for w in ["security", "vulnerability", "secret", "iam", "rbac", "threat"]):
        return "security"
    elif any(w in q for w in ["performance", "latency", "slow", "throughput", "bottleneck", "scale"]):
        return "performance"
    return "health"

@app.post("/api/v1/ai/query", response_model=AIQueryResponse)
async def query_infrastructure(request: AIQueryRequest):
    category = classify_query(request.query)
    kb = KNOWLEDGE_BASE.get(category, KNOWLEDGE_BASE["health"])
    metrics = generate_mock_metrics()

    cluster = request.clusterId or "ke-health-eks-001"

    response_text = kb["response"].format(
        cluster=cluster,
        cpu=metrics["cpu"],
        mem=metrics["mem"],
        current=metrics["current_nodes"],
        recommended=metrics["recommended_nodes"],
        savings=metrics["savings"],
        status=metrics["status"],
        node=metrics["node"],
        rate=metrics["rate"],
        pending=metrics["pending"],
        issues=metrics["issues"],
        score=metrics["score"],
        issues_count=metrics["vuln_count"],
        rotation_status=metrics["rotation_status"],
        latency=metrics["latency"],
        tps=metrics["tps"],
        pod=metrics["pod"],
        replicas=metrics["replicas"]
    )

    return AIQueryResponse(
        response=response_text,
        sources=kb["sources"],
        confidence=random.choice(["HIGH", "HIGH", "MEDIUM"]),
        recommendation=kb["recommendation"]
    )

@app.get("/api/v1/ai/status", response_model=HealthStatus)
async def get_status():
    return HealthStatus(
        status="OPERATIONAL",
        model="gpt-4-mock",
        version="1.0.0",
        capabilities=[
            "cost-optimization",
            "health-analysis",
            "compliance-review",
            "security-assessment",
            "performance-tuning"
        ]
    )

@app.get("/health")
async def health_check():
    return {"status": "healthy", "service": "ai-engine"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
