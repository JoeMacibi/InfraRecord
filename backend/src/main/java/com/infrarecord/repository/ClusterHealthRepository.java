package com.infrarecord.repository;

import com.infrarecord.model.ClusterHealth;
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
public interface ClusterHealthRepository extends JpaRepository<ClusterHealth, String> {

    List<ClusterHealth> findByClusterIdOrderByTimestampDesc(String clusterId);

    @Query("SELECT ch FROM ClusterHealth ch WHERE ch.clusterId = :clusterId AND ch.timestamp >= :since ORDER BY ch.timestamp DESC")
    List<ClusterHealth> findRecentByClusterId(@Param("clusterId") String clusterId, @Param("since") Instant since);

    @Query("SELECT ch FROM ClusterHealth ch WHERE ch.status = 'CRITICAL' AND ch.timestamp >= :since ORDER BY ch.timestamp DESC")
    List<ClusterHealth> findCriticalEvents(@Param("since") Instant since);

    @Query("SELECT DISTINCT ch.clusterId FROM ClusterHealth ch")
    List<String> findAllClusterIds();

    @Query("SELECT ch FROM ClusterHealth ch WHERE ch.timestamp >= :since ORDER BY ch.timestamp DESC")
    Page<ClusterHealth> findAllRecent(Pageable pageable, @Param("since") Instant since);

    Optional<ClusterHealth> findFirstByClusterIdOrderByTimestampDesc(String clusterId);
}
