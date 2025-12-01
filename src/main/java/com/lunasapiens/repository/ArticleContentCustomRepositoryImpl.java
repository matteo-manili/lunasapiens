package com.lunasapiens.repository;

import com.lunasapiens.Utils;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ArticleContentCustomRepositoryImpl implements ArticleContentCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ArticleContentCustomRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void updateSequence() {
        entityManager.createNativeQuery("SELECT setval('article_content_id_seq', (SELECT MAX(id) FROM article_content), true)")
                .getSingleResult();
    }


    /**
     * Full-Text Search (FTS) in PostgreSQL
     * @param keyword
     * @param limit
     * @return
     */
    @Transactional(readOnly = true)
    public List<ArticleContent> searchByKeywordFTS(String keyword, int limit) {
        String sql = "SELECT id, content, created_at, embedding " +
                "FROM article_content " +
                "WHERE to_tsvector('italian', content) @@ plainto_tsquery('italian', ?) " +
                "ORDER BY ts_rank(to_tsvector('italian', content), plainto_tsquery('italian', ?)) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, keyword);  // query per l'operatore @@
            ps.setString(2, keyword);  // query per ts_rank
            ps.setInt(3, limit);
            return ps;
        }, (rs, rowNum) -> mapArticle(rs));
    }






    /** ************************** Full-Text Search (FTS) in PostgreSQL END EMBEDDING ************************/

    @Transactional(readOnly = true)
    public List<ArticleContent> searchByEmbeddingThenFTS(Float[] embedding, String keyword, int limit) throws Exception {
        PGobject pgVector = UtilsRepository.toPgVector(embedding);

        // 1. Trova i più vicini semanticamente
        String sql = "WITH nearest AS (" +
                "  SELECT id, content, created_at, embedding, " +
                "         embedding <=> ? AS cosine_distance " +
                "  FROM article_content " +
                "  ORDER BY cosine_distance " +
                "  LIMIT 100" +  // limitiamo il set per il re-ranking FTS
                ") " +
                // 2. Re-ranking con FTS
                "SELECT *, ts_rank(to_tsvector('italian', content), plainto_tsquery('italian', ?)) AS fts_rank " +
                "FROM nearest " +
                "ORDER BY fts_rank DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, pgVector); // embedding per <->
            ps.setString(2, keyword);  // FTS re-ranking
            ps.setInt(3, limit);
            return ps;
        }, (rs, rowNum) -> mapArticle(rs));
    }



    /** ************************** EMBEDDING ************************/

    @Transactional(readOnly = true)
    public List<ArticleContent> searchByEmbedding(Float[] embedding, int limit) throws Exception {

        // Converti embedding in stringa per PGvector
        PGobject pgVector = UtilsRepository.toPgVector(embedding);

        String sql = "SELECT id, content, created_at, embedding " +
                "FROM article_content " +
                "ORDER BY embedding <=> ? " +
                "LIMIT ?";

        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, pgVector);
            ps.setInt(2, limit);
            return ps;
        }, (rs, rowNum) -> mapArticle(rs));
    }


    private ArticleContent mapArticle(ResultSet rs) throws SQLException {
        ArticleContent article = new ArticleContent();
        article.setId(rs.getLong("id"));
        article.setContent(rs.getString("content"));
        article.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        String val = rs.getString("embedding");
        if (val != null && !val.isBlank()) {
            String[] parts = val.substring(1, val.length() - 1).split(",");
            Float[] vec = new Float[parts.length];
            for (int i = 0; i < parts.length; i++) {
                vec[i] = Float.parseFloat(parts[i]);
            }
            article.setEmbedding(vec);
        }
        return article;
    }


    /**
     * Aggiorna solo il campo embedding di un articolo esistente.
     */
    @Transactional
    public ArticleContent updateArticleEmbeddingJdbc(Long articleId, Float[] embedding) throws Exception {

        // Converti embedding in stringa per PGvector
        PGobject pgVector = UtilsRepository.toPgVector(embedding);

        String sql = "UPDATE article_content SET embedding = ? WHERE id = ?";

        int updatedRows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, pgVector);
            ps.setLong(2, articleId);
            return ps;
        });

        if (updatedRows == 0) {
            throw new IllegalArgumentException("Nessun articolo trovato con id " + articleId);
        }

        // Restituisci un oggetto ArticleContent aggiornato (opzionale)
        ArticleContent article = new ArticleContent();
        article.setId(articleId);
        article.setEmbedding(embedding);
        return article;
    }


    @Transactional
    public ArticleContent saveArticleWithEmbeddingJdbc(String content, Float[] embedding) throws Exception {
        // Converti embedding in stringa per PGvector
        PGobject pgVector = UtilsRepository.toPgVector(embedding);

        String sql = "INSERT INTO article_content (content, created_at, embedding) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, content);
            ps.setObject(2, Utils.getNowRomeEurope().toLocalDateTime());
            ps.setObject(3, pgVector);
            return ps;
        }, keyHolder);

        ArticleContent article = new ArticleContent();
        article.setId(keyHolder.getKey().longValue());
        article.setContent(content);
        article.setCreatedAt(Utils.getNowRomeEurope().toLocalDateTime());
        article.setEmbedding(embedding);

        return article;
    }







}
