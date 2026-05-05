package com.infrarecord.service;

import com.infrarecord.model.ClusterHealth;
import com.infrarecord.model.HealthMetricsPacket;
import com.infrarecord.repository.ClusterHealthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClusterHealthServiceTest {

    @Mock
    private ClusterHealthRepository repository;

    @InjectMocks
    private ClusterHealthService service;

    private HealthMetricsPacket samplePacket;

    @BeforeEach
    void setUp() {
        samplePacket = HealthMetricsPacket.builder()
                .clusterId("ke-health-eks-001")
                .nodeId("ip-10-0-1-45")
                .timestamp(Instant.now())
                .metrics(Map.of("cpu_utilization", 74.5, "memory_utilization", 82.1))
                .activePods(12)
                .status("Healthy")
                .build();
    }

    @Test
    void processHealthPacket_shouldSaveAndReturnEntity() {
        ClusterHealth expected = ClusterHealth.builder()
                .id("test-id")
                .clusterId("ke-health-eks-001")
                .nodeId("ip-10-0-1-45")
                .status(ClusterHealth.HealthStatus.HEALTHY)
                .build();

        when(repository.save(any(ClusterHealth.class))).thenReturn(expected);

        ClusterHealth result = service.processHealthPacket(samplePacket);

        assertThat(result).isNotNull();
        assertThat(result.getClusterId()).isEqualTo("ke-health-eks-001");
        assertThat(result.getStatus()).isEqualTo(ClusterHealth.HealthStatus.HEALTHY);
    }
}
