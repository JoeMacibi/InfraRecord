package com.infrarecord.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infrarecord.model.AuditEvent;
import com.infrarecord.model.HealthMetricsPacket;
import com.infrarecord.service.AuditService;
import com.infrarecord.service.ClusterHealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InfraRecordConsumer {

    private final ClusterHealthService clusterHealthService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.health-metrics}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeHealthMetrics(@Payload HealthMetricsPacket packet,
                                      @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Received health metrics for cluster: {}, partition key: {}", packet.getClusterId(), key);
        try {
            clusterHealthService.processHealthPacket(packet);
            log.info("Health metrics processed successfully for cluster: {}", packet.getClusterId());
        } catch (Exception e) {
            log.error("Failed to process health metrics: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.audit-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAuditEvents(@Payload String auditJson,
                                    @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Received audit event with key: {}", key);
        try {
            AuditEvent event = objectMapper.readValue(auditJson, AuditEvent.class);
            auditService.recordEvent(event);
            log.info("Audit event recorded: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Failed to process audit event: {}", e.getMessage(), e);
        }
    }
}
