import pytest
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_health_check():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "healthy"


def test_ai_status():
    response = client.get("/api/v1/ai/status")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "OPERATIONAL"
    assert "cost-optimization" in data["capabilities"]


def test_ai_query_cost():
    response = client.post("/api/v1/ai/query", json={
        "query": "How can I reduce costs on cluster eks-001?",
        "clusterId": "ke-health-eks-001"
    })
    assert response.status_code == 200
    data = response.json()
    assert "response" in data
    assert data["recommendation"] == "DOWNSIZE"
    assert len(data["sources"]) > 0


def test_ai_query_health():
    response = client.post("/api/v1/ai/query", json={
        "query": "What is the health status of all clusters?"
    })
    assert response.status_code == 200
    data = response.json()
    assert data["recommendation"] == "MONITOR"


def test_ai_query_compliance():
    response = client.post("/api/v1/ai/query", json={
        "query": "Show compliance failures from this week"
    })
    assert response.status_code == 200
    data = response.json()
    assert data["recommendation"] == "REVIEW"


def test_ai_query_unknown():
    response = client.post("/api/v1/ai/query", json={
        "query": "Tell me about the weather"
    })
    assert response.status_code == 200
    data = response.json()
    assert data["recommendation"] == "CLARIFY"
