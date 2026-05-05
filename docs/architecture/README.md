# System Architecture

## Overview

InfraRecord follows a microservices architecture with event-driven data ingestion.

## Data Flow

```
Telemetry Generator / Real Clusters
         │
         ▼
    ┌─────────┐
    │  Kafka  │
    │ Topics  │
    └────┬────┘
         │
    ┌────┴────┐
    │ Spring  │
    │  Boot   │
    │Consumer │
    └────┬────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
PostgreSQL  Prometheus
(Audit)     (Metrics)
    │         │
    └────┬────┘
         │
    ┌────┴────┐
    │  React  │
    │Frontend │
    └─────────┘
```

## Component Details

### Data Ingestion Layer
- **Kafka Topics**: `cluster-health-metrics` (6 partitions), `infrastructure-audit-events` (3 partitions)
- **Producers**: Telemetry generator, real cluster agents, Terraform hooks
- **Schema**: JSON with cluster_id, node_id, timestamp, metrics

### Processing Layer
- **Spring Boot Consumer**: Multi-threaded Kafka consumer with error handling
- **Anomaly Detection**: Threshold-based alerting (CPU > 85%, Memory > 88%)
- **Persistence**: JPA/Hibernate with PostgreSQL, Flyway migrations

### Storage Layer
- **PostgreSQL**: Audit events, cluster health history, compliance records
- **Prometheus**: Time-series metrics, Spring Boot actuator endpoints
- **Retention**: 30-day rolling window for audit, configurable for metrics

### Presentation Layer
- **React Dashboard**: Real-time updates via React Query polling
- **AI Chat**: Natural language interface with mock RAG responses
- **Grafana**: Advanced metric visualization and alerting

## Security Considerations

1. **Secrets Management**: Kubernetes secrets for DB credentials, JWT keys
2. **Network Policies**: Restrict inter-service communication
3. **Audit Immutability**: Audit events are append-only with event_id uniqueness
4. **CORS**: Configured for local development; restrict in production

## Scalability

- **Horizontal**: Backend replicas behind K8s service, Kafka partition scaling
- **Vertical**: Resource limits defined in manifests
- **Database**: Connection pooling (HikariCP), indexed queries
