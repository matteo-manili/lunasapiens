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

@Repository
public class ChunksCustomRepositoryImpl implements ChunksCustomRepository {



    @PersistenceContext
    private EntityManager entityManager;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ChunksCustomRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    /**
     * Salva un chunk nel database con embedding.
     */
    @Transactional
    public Chunks saveChunkJdbc(Long videoId, int chunkIndex, String content, Float[] embedding) throws Exception {

        PGobject pgVector = UtilRepository.toPgVector(embedding);

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
