package com.lunasapiens.repository;


import com.lunasapiens.entity.Chunks;
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
import java.util.List;

@Repository
public class ChunksCustomRepositoryImpl implements ChunksCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ChunksCustomRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }





    public List<Chunks> findNearestChunksWithFts(Float[] embedding, String userQuestion, int limit) throws Exception {
        PGobject pgVector = UtilsRepository.toPgVector(embedding);

        String sql =
                "WITH nearest AS ( " +
                        "   SELECT id, video_id, chunk_index, content, embedding, " +
                        "          embedding <-> ? AS distance " +
                        "   FROM chunks " +
                        "   ORDER BY distance " +
                        "   LIMIT 50 " +   // 🔹 prendo i 50 più vicini semanticamente
                        ") " +
                        "SELECT id, video_id, chunk_index, content, embedding, " +
                        "       ts_rank(to_tsvector('italian', content), plainto_tsquery('italian', ?)) AS fts_rank, " +
                        "       distance " +
                        "FROM nearest " +
                        "ORDER BY fts_rank DESC, distance ASC " +
                        "LIMIT ?";

        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, pgVector);        // embedding utente
            ps.setString(2, userQuestion);    // testo della domanda per FTS
            ps.setInt(3, limit);              // numero massimo di chunk
            return ps;
        }, (rs, rowNum) -> {
            Chunks chunk = new Chunks();
            chunk.setId(rs.getLong("id"));
            chunk.setVideoId(rs.getLong("video_id"));
            chunk.setChunkIndex(rs.getInt("chunk_index"));
            chunk.setContent(rs.getString("content"));
            // embedding non lo ricostruisco da DB (lo lasciamo null per evitare overhead inutile)
            return chunk;
        });
    }




    public List<Chunks> findNearestChunks(Float[] embedding, int limit) throws Exception {
        PGobject pgVector = UtilsRepository.toPgVector(embedding);

        String sql = "SELECT id, video_id, chunk_index, content, embedding " +
                "FROM chunks " +
                "ORDER BY embedding <-> ? " +
                "LIMIT ?";

        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, pgVector);
            ps.setInt(2, limit);
            return ps;
        }, (rs, rowNum) -> {
            Chunks chunk = new Chunks();
            chunk.setId(rs.getLong("id"));
            chunk.setVideoId(rs.getLong("video_id"));
            chunk.setChunkIndex(rs.getInt("chunk_index"));
            chunk.setContent(rs.getString("content"));
            return chunk;
        });
    }










    /**
     * Salva un chunk nel database con embedding.
     */
    @Transactional
    public Chunks saveChunkJdbc(Long videoId, int chunkIndex, String content, Float[] embedding) throws Exception {

        PGobject pgVector = UtilsRepository.toPgVector(embedding);

        String sql = "INSERT INTO chunks (video_id, chunk_index, content, embedding) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, videoId);
            ps.setInt(2, chunkIndex);
            ps.setString(3, content);
            ps.setObject(4, pgVector);
            return ps;
        }, keyHolder);

        Chunks chunk = new Chunks();
        chunk.setId(keyHolder.getKey().longValue());
        chunk.setVideoId(videoId);
        chunk.setChunkIndex(chunkIndex);
        chunk.setContent(content);
        chunk.setEmbedding(embedding);

        return chunk;
    }







}
