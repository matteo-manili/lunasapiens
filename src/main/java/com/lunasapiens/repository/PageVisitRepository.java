package com.lunasapiens.repository;

import com.lunasapiens.entity.PageVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PageVisitRepository extends JpaRepository<PageVisit, Long> {


    PageVisit findTopBySessionIdOrderByIdDesc(String sessionId);

    List<PageVisit> findAllByOrderByIdDesc();

}


