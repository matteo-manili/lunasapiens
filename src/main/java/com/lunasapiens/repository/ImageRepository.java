package com.lunasapiens.repository;

import com.lunasapiens.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findFirstByOrderByIdDesc();

    @Query("SELECT o FROM Image o WHERE o.id = :id")
    Optional<Image> findById(@Param("id") String id);





}


