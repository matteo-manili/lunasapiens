package com.lunasapiens.repository;

import com.lunasapiens.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findFirstByOrderByIdDesc();

    @Query("SELECT o FROM Article o WHERE o.id = :id")
    Optional<Article> findById(@Param("id") String id);





}


