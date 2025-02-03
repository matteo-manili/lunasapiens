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



    // Query personalizzata per trovare un articolo per id
    @Query("SELECT o FROM ArticleContent o WHERE o.id = :id")
    Optional<ArticleContent> findById(@Param("id") Long id);

    // Query personalizzata per trovare tutti gli articoli ordinati per id in ordine decrescente
    @Query("SELECT a FROM ArticleContent a ORDER BY a.createdAt DESC")
    List<ArticleContent> findAllByOrderByCreatedAtDesc();  // Usando la colonna createdAt per ordinare

}


