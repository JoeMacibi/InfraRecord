package com.infrarecord.controller;

import com.infrarecord.model.AIQueryRequest;
import com.infrarecord.model.AIQueryResponse;
import com.infrarecord.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI Engine", description = "RAG-powered infrastructure intelligence")
public class AIController {

    private final AIService aiService;

    @PostMapping("/query")
    @Operation(summary = "Query infrastructure with natural language")
    public ResponseEntity<AIQueryResponse> query(@RequestBody AIQueryRequest request) {
        return ResponseEntity.ok(aiService.queryInfrastructure(request));
    }

    @GetMapping("/status")
    @Operation(summary = "Check AI engine status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(Map.of(
                "status", "OPERATIONAL",
                "model", "gpt-4-mock",
                "version", "1.0.0",
                "capabilities", List.of("cost-optimization", "health-analysis", "compliance-review")
        ));
    }
}
