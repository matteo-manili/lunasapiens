package com.lunasapiens.repository;


import com.lunasapiens.entity.VideoChunks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoChunksRepository extends JpaRepository<VideoChunks, Long> {



}