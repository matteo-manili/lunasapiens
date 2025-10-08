package com.lunasapiens.repository;


import com.lunasapiens.entity.VideoChunks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface VideoChunksRepository extends JpaRepository<VideoChunks, Long> {



    @Modifying
    @Transactional
    @Query(value = "UPDATE video_chunks SET metadati = CAST(:metadati AS jsonb) WHERE id = :id", nativeQuery = true)
    void updateMetadati(@Param("id") Long id, @Param("metadati") String metadati);


    @Query("SELECT v.title FROM VideoChunks v ORDER BY v.numeroVideo ASC")
    List<String> findAllTitles();



}