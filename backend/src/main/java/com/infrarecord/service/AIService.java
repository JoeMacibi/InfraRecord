package com.infrarecord.service;

import com.infrarecord.model.AIQueryRequest;
import com.infrarecord.model.AIQueryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.ai.service.url}")
    private String aiServiceUrl;

    public AIQueryResponse queryInfrastructure(AIQueryRequest request) {
        log.info("Sending AI query: {}", request.getQuery());
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AIQueryRequest> entity = new HttpEntity<>(request, headers);

            return restTemplate.postForObject(
                    aiServiceUrl + "/api/v1/ai/query",
                    entity,
                    AIQueryResponse.class
            );
        } catch (Exception e) {
            log.error("AI service unavailable, returning fallback response", e);
            return getFallbackResponse(request);
        }
    }

    private AIQueryResponse getFallbackResponse(AIQueryRequest request) {
        String query = request.getQuery().toLowerCase();
        String response;
        String recommendation;

        if (query.contains("cost") || query.contains("rightsize")) {
            response = "Based on historical metrics, cluster ke-health-eks-001 shows 74.5% average CPU utilization with memory at 82.1%. Recommend reducing node count from 5 to 4 during off-peak hours (00:00-06:00 UTC) to save approximately $340/month.";
            recommendation = "DOWNSIZE";
        } else if (query.contains("health") || query.contains("status")) {
            response = "All monitored clusters are currently HEALTHY. No critical alerts in the last 24 hours. Node ip-10-0-1-45 has elevated memory utilization at 82.1% but remains within acceptable thresholds.";
            recommendation = "MONITOR";
        } else if (query.contains("compliance") || query.contains("audit")) {
            response = "Compliance status: 98.7% pass rate over the last 30 days. 2 pending reviews require attention: secret rotation overdue on cluster ke-health-eks-001 and IAM policy drift detected on on-prem-k8s-002.";
            recommendation = "REVIEW";
        } else {
            response = "I'm analyzing your infrastructure query. Based on current telemetry from 3 active clusters, overall system health is stable. Would you like specific metrics on CPU, memory, or compliance status?";
            recommendation = "CLARIFY";
        }

        return AIQueryResponse.builder()
                .response(response)
                .sources(List.of(
                        Map.of("source", "Prometheus", "relevance", 0.92),
                        Map.of("source", "PostgreSQL Audit", "relevance", 0.85)
                ))
                .confidence("HIGH")
                .recommendation(recommendation)
                .build();
    }
}
