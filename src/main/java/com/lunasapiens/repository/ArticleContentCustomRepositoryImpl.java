package com.lunasapiens.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleContentCustomRepositoryImpl implements ArticleContentCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public void updateSequence() {
        entityManager.createNativeQuery("SELECT setval('article_content_id_seq', (SELECT MAX(id) FROM article_content), true)")
                .getSingleResult();
    }


}
