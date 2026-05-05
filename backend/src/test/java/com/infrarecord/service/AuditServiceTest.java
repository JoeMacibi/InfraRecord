package com.infrarecord.service;

import com.infrarecord.model.AuditEvent;
import com.infrarecord.repository.AuditEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditEventRepository repository;

    @InjectMocks
    private AuditService service;

    @Test
    void recordEvent_shouldSaveAuditEvent() {
        AuditEvent event = AuditEvent.builder()
                .eventId("audit-9982")
                .user("j.macibi")
                .action(AuditEvent.ActionType.TERRAFORM_APPLY)
                .resource("eks_node_group")
                .changeSummary("Scaled from 3 to 5 nodes")
                .complianceStatus(AuditEvent.ComplianceStatus.PASSED)
                .timestamp(Instant.now())
                .build();

        when(repository.save(any(AuditEvent.class))).thenReturn(event);

        AuditEvent result = service.recordEvent(event);

        assertThat(result.getEventId()).isEqualTo("audit-9982");
        assertThat(result.getComplianceStatus()).isEqualTo(AuditEvent.ComplianceStatus.PASSED);
    }
}
