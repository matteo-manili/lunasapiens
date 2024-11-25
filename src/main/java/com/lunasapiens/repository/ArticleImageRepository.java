package com.lunasapiens.repository;

import com.lunasapiens.entity.ArticleImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleImageRepository extends JpaRepository<ArticleImage, Long> {

    Optional<ArticleImage> findFirstByOrderByIdDesc();

    @Query("SELECT o FROM ArticleImage o WHERE o.id = :id")
    Optional<ArticleImage> findById(@Param("id") String id);





}


