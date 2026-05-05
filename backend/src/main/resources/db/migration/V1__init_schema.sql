CREATE TABLE IF NOT EXISTS cluster_health (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cluster_id VARCHAR(255) NOT NULL,
    node_id VARCHAR(255) NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    active_pods INTEGER,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS cluster_metrics (
    health_id UUID REFERENCES cluster_health(id) ON DELETE CASCADE,
    metric_name VARCHAR(255) NOT NULL,
    metric_value VARCHAR(500),
    PRIMARY KEY (health_id, metric_name)
);

CREATE TABLE IF NOT EXISTS audit_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id VARCHAR(255) NOT NULL UNIQUE,
    user_name VARCHAR(255) NOT NULL,
    action VARCHAR(100) NOT NULL,
    resource VARCHAR(500) NOT NULL,
    change_summary TEXT,
    compliance_status VARCHAR(50) NOT NULL,
    cluster_id VARCHAR(255),
    timestamp TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_cluster_health_cluster_id ON cluster_health(cluster_id);
CREATE INDEX idx_cluster_health_timestamp ON cluster_health(timestamp);
CREATE INDEX idx_cluster_health_status ON cluster_health(status);
CREATE INDEX idx_audit_events_event_id ON audit_events(event_id);
CREATE INDEX idx_audit_events_user ON audit_events(user_name);
CREATE INDEX idx_audit_events_timestamp ON audit_events(timestamp);
CREATE INDEX idx_audit_events_compliance ON audit_events(compliance_status);
