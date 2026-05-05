package com.infrarecord.controller;

import com.infrarecord.model.ClusterHealth;
import com.infrarecord.model.HealthMetricsPacket;
import com.infrarecord.service.ClusterHealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@Tag(name = "Health Metrics", description = "Cluster health monitoring endpoints")
public class HealthController {

    private final ClusterHealthService clusterHealthService;

    @PostMapping("/ingest")
    @Operation(summary = "Ingest health metrics (testing endpoint)")
    public ResponseEntity<ClusterHealth> ingestHealth(@RequestBody HealthMetricsPacket packet) {
        return ResponseEntity.ok(clusterHealthService.processHealthPacket(packet));
    }

    @GetMapping("/clusters/{clusterId}")
    @Operation(summary = "Get health history for a cluster")
    public ResponseEntity<List<ClusterHealth>> getClusterHealth(@PathVariable String clusterId) {
        return ResponseEntity.ok(clusterHealthService.getClusterHealth(clusterId));
    }

    @GetMapping("/clusters/{clusterId}/latest")
    @Operation(summary = "Get latest health for a cluster")
    public ResponseEntity<?> getLatestHealth(@PathVariable String clusterId) {
        return clusterHealthService.getLatestHealth(clusterId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/critical")
    @Operation(summary = "Get critical health events")
    public ResponseEntity<Map<String, Object>> getCriticalEvents(
            @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(Map.of(
                "events", clusterHealthService.getCriticalEvents(hours),
                "count", clusterHealthService.getCriticalEvents(hours).size(),
                "periodHours", hours
        ));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all recent health records")
    public ResponseEntity<Page<ClusterHealth>> getAllHealth(Pageable pageable,
                                                             @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(clusterHealthService.getAllRecent(pageable, hours));
    }
}
