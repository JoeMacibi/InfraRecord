package com.infrarecord.controller;

import com.infrarecord.model.AuditEvent;
import com.infrarecord.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Compliance audit trail management")
public class AuditController {

    private final AuditService auditService;

    @PostMapping("/events")
    @Operation(summary = "Record a new audit event")
    public ResponseEntity<AuditEvent> recordEvent(@RequestBody AuditEvent event) {
        return ResponseEntity.ok(auditService.recordEvent(event));
    }

    @GetMapping("/events/{eventId}")
    @Operation(summary = "Get audit event by ID")
    public ResponseEntity<AuditEvent> getEvent(@PathVariable String eventId) {
        return auditService.getEventById(eventId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/events")
    @Operation(summary = "Get all recent audit events")
    public ResponseEntity<Page<AuditEvent>> getAllEvents(Pageable pageable,
                                                          @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(auditService.getAllRecent(pageable, days));
    }

    @GetMapping("/failed")
    @Operation(summary = "Get failed compliance events")
    public ResponseEntity<Map<String, Object>> getFailedEvents(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(Map.of(
                "events", auditService.getFailedComplianceEvents(days),
                "count", auditService.getFailedComplianceEvents(days).size(),
                "periodDays", days
        ));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get compliance dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(auditService.getComplianceDashboard());
    }

    @GetMapping("/breakdown")
    @Operation(summary = "Get action type breakdown")
    public ResponseEntity<Map<String, Long>> getBreakdown(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(auditService.getActionBreakdown(days));
    }
}
