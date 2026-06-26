package com.lunasapiens.repository;

import com.lunasapiens.entity.PageVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface PageVisitRepository extends JpaRepository<PageVisit, Long> {


    List<PageVisit> findAllByOrderByIdDesc();


    @Modifying
    @Transactional
    @Query("""
        update PageVisit v
        set v.lastSeen = :now
        where v.sessionId = :sessionId
        and v.endTime is null
    """)
    void updateHeartbeat(String sessionId, LocalDateTime now);


    @Modifying
    @Transactional
    @Query("""
        update PageVisit v
        set v.endTime = v.lastSeen
        where v.endTime is null
        and v.lastSeen < :cutoff
    """)
    void closeInactiveSessions(LocalDateTime cutoff);

}


