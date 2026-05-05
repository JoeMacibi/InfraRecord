package com.infrarecord.service;

import com.infrarecord.model.AuditEvent;
import com.infrarecord.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditEventRepository repository;

    @Transactional
    public AuditEvent recordEvent(AuditEvent event) {
        log.info("Recording audit event: {} by user: {}", event.getEventId(), event.getUser());
        return repository.save(event);
    }

    @Transactional(readOnly = true)
    public Optional<AuditEvent> getEventById(String eventId) {
        return repository.findByEventId(eventId);
    }

    @Transactional(readOnly = true)
    public List<AuditEvent> getEventsByUser(String user) {
        return repository.findByUserOrderByTimestampDesc(user);
    }

    @Transactional(readOnly = true)
    public List<AuditEvent> getFailedComplianceEvents(int daysBack) {
        Instant since = Instant.now().minus(daysBack, ChronoUnit.DAYS);
        return repository.findFailedComplianceEvents(since);
    }

    @Transactional(readOnly = true)
    public Page<AuditEvent> getAllRecent(Pageable pageable, int daysBack) {
        Instant since = Instant.now().minus(daysBack, ChronoUnit.DAYS);
        return repository.findAllRecent(pageable, since);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getComplianceDashboard() {
        Instant since = Instant.now().minus(30, ChronoUnit.DAYS);
        Map<String, Object> dashboard = new HashMap<>();

        Long passed = repository.countByComplianceStatusAndTimestampAfter(AuditEvent.ComplianceStatus.PASSED, since);
        Long failed = repository.countByComplianceStatusAndTimestampAfter(AuditEvent.ComplianceStatus.FAILED, since);
        Long pending = repository.countByComplianceStatusAndTimestampAfter(AuditEvent.ComplianceStatus.PENDING_REVIEW, since);

        dashboard.put("passed", passed);
        dashboard.put("failed", failed);
        dashboard.put("pendingReview", pending);
        dashboard.put("total", passed + failed + pending);
        dashboard.put("passRate", passed + failed + pending > 0 ? (double) passed / (passed + failed + pending) * 100 : 0.0);
        dashboard.put("period", "Last 30 days");

        List<AuditEvent> recentFailures = getFailedComplianceEvents(7);
        dashboard.put("recentFailures", recentFailures.stream()
                .map(e -> Map.of(
                        "eventId", e.getEventId(),
                        "user", e.getUser(),
                        "action", e.getAction(),
                        "resource", e.getResource(),
                        "timestamp", e.getTimestamp()
                ))
                .collect(Collectors.toList()));

        return dashboard;
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getActionBreakdown(int daysBack) {
        Instant since = Instant.now().minus(daysBack, ChronoUnit.DAYS);
        List<Object[]> results = repository.countByActionSince(since);
        Map<String, Long> breakdown = new HashMap<>();
        for (Object[] row : results) {
            breakdown.put(row[0].toString(), (Long) row[1]);
        }
        return breakdown;
    }
}
