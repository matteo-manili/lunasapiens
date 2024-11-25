package com.lunasapiens.repository;

import com.lunasapiens.entity.ArticleContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleContentRepository extends JpaRepository<ArticleContent, Long> {

    Optional<ArticleContent> findFirstByOrderByIdDesc();

    @Query("SELECT o FROM ArticleContent o WHERE o.id = :id")
    Optional<ArticleContent> findById(@Param("id") String id);

    @Query("SELECT a FROM ArticleContent a ORDER BY a.id DESC")
    List<ArticleContent> findAllByOrderByIdDesc();



}


