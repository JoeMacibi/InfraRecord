package com.infrarecord.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topics.health-metrics}")
    private String healthMetricsTopic;

    @Value("${app.kafka.topics.audit-events}")
    private String auditEventsTopic;

    @Bean
    public NewTopic healthMetricsTopic() {
        return TopicBuilder.name(healthMetricsTopic)
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic auditEventsTopic() {
        return TopicBuilder.name(auditEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
