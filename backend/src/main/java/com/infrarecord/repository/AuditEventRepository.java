package com.infrarecord.repository;

import com.infrarecord.model.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, String> {

    Optional<AuditEvent> findByEventId(String eventId);

    List<AuditEvent> findByUserOrderByTimestampDesc(String user);

    List<AuditEvent> findByComplianceStatusOrderByTimestampDesc(AuditEvent.ComplianceStatus status);

    @Query("SELECT ae FROM AuditEvent ae WHERE ae.timestamp >= :since ORDER BY ae.timestamp DESC")
    Page<AuditEvent> findAllRecent(Pageable pageable, @Param("since") Instant since);

    @Query("SELECT ae FROM AuditEvent ae WHERE ae.complianceStatus = 'FAILED' AND ae.timestamp >= :since ORDER BY ae.timestamp DESC")
    List<AuditEvent> findFailedComplianceEvents(@Param("since") Instant since);

    @Query("SELECT COUNT(ae) FROM AuditEvent ae WHERE ae.complianceStatus = :status AND ae.timestamp >= :since")
    Long countByComplianceStatusAndTimestampAfter(@Param("status") AuditEvent.ComplianceStatus status, @Param("since") Instant since);

    @Query("SELECT ae.action, COUNT(ae) FROM AuditEvent ae WHERE ae.timestamp >= :since GROUP BY ae.action")
    List<Object[]> countByActionSince(@Param("since") Instant since);
}
