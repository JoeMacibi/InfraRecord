package com.infrarecord.service;

import com.infrarecord.model.ClusterHealth;
import com.infrarecord.model.HealthMetricsPacket;
import com.infrarecord.repository.ClusterHealthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusterHealthService {

    private final ClusterHealthRepository repository;

    @Transactional
    public ClusterHealth processHealthPacket(HealthMetricsPacket packet) {
        log.info("Processing health packet for cluster: {}, node: {}", packet.getClusterId(), packet.getNodeId());

        ClusterHealth.HealthStatus status = parseStatus(packet.getStatus());

        Map<String, String> metrics = new HashMap<>();
        if (packet.getMetrics() != null) {
            packet.getMetrics().forEach((k, v) -> metrics.put(k, v.toString()));
        }

        ClusterHealth health = ClusterHealth.builder()
                .clusterId(packet.getClusterId())
                .nodeId(packet.getNodeId())
                .timestamp(packet.getTimestamp() != null ? packet.getTimestamp() : Instant.now())
                .metrics(metrics)
                .activePods(packet.getActivePods())
                .status(status)
                .build();

        return repository.save(health);
    }

    @Transactional(readOnly = true)
    public List<ClusterHealth> getClusterHealth(String clusterId) {
        return repository.findByClusterIdOrderByTimestampDesc(clusterId);
    }

    @Transactional(readOnly = true)
    public Optional<ClusterHealth> getLatestHealth(String clusterId) {
        return repository.findFirstByClusterIdOrderByTimestampDesc(clusterId);
    }

    @Transactional(readOnly = true)
    public List<ClusterHealth> getCriticalEvents(int hoursBack) {
        Instant since = Instant.now().minus(hoursBack, ChronoUnit.HOURS);
        return repository.findCriticalEvents(since);
    }

    @Transactional(readOnly = true)
    public Page<ClusterHealth> getAllRecent(Pageable pageable, int hoursBack) {
        Instant since = Instant.now().minus(hoursBack, ChronoUnit.HOURS);
        return repository.findAllRecent(pageable, since);
    }

    @Transactional(readOnly = true)
    public List<String> getAllClusters() {
        return repository.findAllClusterIds();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getClusterSummary() {
        List<String> clusters = getAllClusters();
        Map<String, Object> summary = new HashMap<>();

        List<Map<String, Object>> clusterSummaries = clusters.stream().map(clusterId -> {
            Map<String, Object> cs = new HashMap<>();
            cs.put("clusterId", clusterId);
            getLatestHealth(clusterId).ifPresent(health -> {
                cs.put("status", health.getStatus());
                cs.put("lastUpdated", health.getTimestamp());
                cs.put("activePods", health.getActivePods());
                cs.put("metrics", health.getMetrics());
            });
            return cs;
        }).collect(Collectors.toList());

        summary.put("clusters", clusterSummaries);
        summary.put("totalClusters", clusters.size());
        summary.put("criticalCount", getCriticalEvents(24).size());
        summary.put("timestamp", Instant.now());

        return summary;
    }

    private ClusterHealth.HealthStatus parseStatus(String status) {
        if (status == null) return ClusterHealth.HealthStatus.UNKNOWN;
        try {
            return ClusterHealth.HealthStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ClusterHealth.HealthStatus.UNKNOWN;
        }
    }
}
