package com.lunasapiens.repository;

import com.lunasapiens.entity.ArticleContent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
public class ArticleContentCustomRepositoryImpl implements ArticleContentCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;




    @Override
    public void updateSequence() {
        entityManager.createNativeQuery("SELECT setval('article_content_id_seq', (SELECT MAX(id) FROM article_content), true)")
                .getSingleResult();
    }


    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ArticleContentCustomRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Transactional
    public ArticleContent saveArticleWithEmbeddingJdbc(String content, float[] embedding) throws SQLException {
        // Costruisci manualmente la stringa per PGvector
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(embedding[i]);
        }
        sb.append("]");
        String vectorLiteral = sb.toString();

        String sql = "INSERT INTO article_content (content, created_at, embedding) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PGobject pgVector = new PGobject();
            pgVector.setType("vector");
            pgVector.setValue(vectorLiteral);

            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, content);
            ps.setObject(2, LocalDateTime.now());
            ps.setObject(3, pgVector);
            return ps;
        }, keyHolder);

        ArticleContent article = new ArticleContent();
        article.setId(keyHolder.getKey().longValue());
        article.setContent(content);
        article.setCreatedAt(LocalDateTime.now());
        article.setEmbedding(embedding);

        return article;
    }

    @Transactional(readOnly = true)
    public List<ArticleContent> findNearestJdbc(float[] embedding, int limit) throws SQLException {
        // Converti embedding in stringa per PGvector
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(embedding[i]);
        }
        sb.append("]");
        String vectorLiteral = sb.toString();

        String sql = "SELECT id, content, created_at, embedding " +
                "FROM article_content " +
                "ORDER BY embedding <-> ? " +
                "LIMIT ?";

        return jdbcTemplate.query(connection -> {
            PGobject pgVector = new PGobject();
            pgVector.setType("vector");
            pgVector.setValue(vectorLiteral);

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, pgVector);
            ps.setInt(2, limit);
            return ps;
        }, (rs, rowNum) -> {
            ArticleContent article = new ArticleContent();
            article.setId(rs.getLong("id"));
            article.setContent(rs.getString("content"));
            article.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));

            // Parsing embedding
            String val = rs.getString("embedding"); // "[0.1,0.2,...]"
            if (val != null && !val.isBlank()) {
                String[] parts = val.substring(1, val.length() - 1).split(",");
                float[] vec = new float[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    vec[i] = Float.parseFloat(parts[i]);
                }
                article.setEmbedding(vec);
            }

            return article;
        });
    }



    /*********************** OLD ***********************/


    @Transactional
    public ArticleContent saveArticleWithEmbeddingJdbc_OLD(String content, float[] embedding) throws SQLException {
        String vectorLiteral = IntStream.range(0, embedding.length)
                .mapToObj(i -> Float.toString(embedding[i]))
                .collect(Collectors.joining(",", "[", "]"));

        String sql = "INSERT INTO article_content (content, created_at, embedding) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PGobject pgVector = new PGobject();
            pgVector.setType("vector");
            pgVector.setValue(vectorLiteral);

            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, content);
            ps.setObject(2, LocalDateTime.now());
            ps.setObject(3, pgVector);
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();

        ArticleContent article = new ArticleContent();
        article.setId(id);
        article.setContent(content);
        article.setCreatedAt(LocalDateTime.now());
        article.setEmbedding(embedding);

        return article;
    }



    @Transactional(readOnly = true)
    public List<ArticleContent> findNearestJdbc_OLD(float[] embedding, int limit) throws SQLException {
        // 1. Converte float[] in literal PostgreSQL vector
        String vectorLiteral = IntStream.range(0, embedding.length)
                .mapToObj(i -> Float.toString(embedding[i]))
                .collect(Collectors.joining(",", "[", "]"));

        // 2. Query parametrizzata (PGobject passerà il tipo vector)
        String sql = "SELECT id, content, created_at, embedding " +
                "FROM article_content " +
                "ORDER BY embedding <-> ? " +
                "LIMIT ?";

        return jdbcTemplate.query(connection -> {
            PGobject pgVector = new PGobject();
            pgVector.setType("vector");
            pgVector.setValue(vectorLiteral);

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, pgVector);
            ps.setInt(2, limit);
            return ps;
        }, (rs, rowNum) -> {
            ArticleContent article = new ArticleContent();
            article.setId(rs.getLong("id"));
            article.setContent(rs.getString("content"));
            article.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));

            // 3. Recupera e converte l'embedding (stile Java 15)
            Object embeddingObj = rs.getObject("embedding");
            if (embeddingObj instanceof PGobject) {
                PGobject pg = (PGobject) embeddingObj;
                String val = pg.getValue(); // "[1.0,2.0,3.0]"
                if (val != null && !val.isBlank()) {
                    val = val.replaceAll("[\\[\\]]", "");
                    String[] parts = val.split(",");
                    float[] vec = new float[parts.length];
                    for (int i = 0; i < parts.length; i++) {
                        vec[i] = Float.parseFloat(parts[i]);
                    }
                    article.setEmbedding(vec);
                }
            }

            return article;
        });
    }




}
