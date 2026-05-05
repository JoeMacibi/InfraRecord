package com.infrarecord.controller;

import com.infrarecord.service.ClusterHealthService;
import com.infrarecord.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Multi-cluster observability dashboard")
public class DashboardController {

    private final ClusterHealthService clusterHealthService;
    private final AuditService auditService;

    @GetMapping("/summary")
    @Operation(summary = "Get overall cluster health summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        return ResponseEntity.ok(clusterHealthService.getClusterSummary());
    }

    @GetMapping("/clusters")
    @Operation(summary = "List all monitored clusters")
    public ResponseEntity<Map<String, Object>> getClusters() {
        return ResponseEntity.ok(Map.of(
                "clusters", clusterHealthService.getAllClusters(),
                "count", clusterHealthService.getAllClusters().size()
        ));
    }

    @GetMapping("/compliance")
    @Operation(summary = "Get compliance dashboard metrics")
    public ResponseEntity<Map<String, Object>> getCompliance() {
        return ResponseEntity.ok(auditService.getComplianceDashboard());
    }

    @GetMapping("/alerts")
    @Operation(summary = "Get active critical alerts")
    public ResponseEntity<Map<String, Object>> getAlerts() {
        return ResponseEntity.ok(Map.of(
                "critical", clusterHealthService.getCriticalEvents(24),
                "failedCompliance", auditService.getFailedComplianceEvents(7),
                "timestamp", java.time.Instant.now()
        ));
    }
}
