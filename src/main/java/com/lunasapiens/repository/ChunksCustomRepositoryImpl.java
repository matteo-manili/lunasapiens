package com.lunasapiens.repository;


import com.lunasapiens.entity.Chunks;
import com.lunasapiens.entity.VideoChunks;
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
                        "   SELECT c.id, c.numero_video_chunks, c.chunk_index, c.content, c.embedding, " +
                        "          vc.title AS video_title, vc.fullContent AS video_full_content, " +
                        "          c.embedding <-> ? AS distance " +
                        "   FROM chunks c " +
                        "   JOIN video_chunks vc ON c.video_chunks_id = vc.numero_video " +
                        "   ORDER BY distance " +
                        "   LIMIT 150 " +
                        ") " +
                        "SELECT id, numero_video_chunks, chunk_index, content, embedding, video_title, video_full_content, " +
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

            // Creiamo VideoChunks popolando anche titolo e fullContent
            VideoChunks videoChunks = new VideoChunks();
            videoChunks.setNumeroVideo(rs.getLong("numero_video_chunks")); // prima era setId
            videoChunks.setTitle(rs.getString("video_title"));
            videoChunks.setFullContent(rs.getString("video_full_content"));
            chunk.setVideoChunks(videoChunks);


            chunk.setChunkIndex(rs.getInt("chunk_index"));
            chunk.setContent(rs.getString("content"));
            // embedding non lo ricostruisco da DB (lo lasciamo null per evitare overhead)
            return chunk;
        });

    }




    public List<Chunks> findNearestChunks(Float[] embedding, int limit) throws Exception {
        PGobject pgVector = UtilsRepository.toPgVector(embedding);

        String sql =
                "SELECT c.id, c.numero_video_chunks, c.chunk_index, c.content, c.embedding, " +
                        "       vc.title AS video_title, vc.fullContent AS video_full_content " +
                        "FROM chunks c " +
                        "JOIN video_chunks vc ON c.video_chunks_id = vc.numero_video " +
                        "ORDER BY c.embedding <-> ? " +
                        "LIMIT ?";

        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, pgVector);   // embedding utente
            ps.setInt(2, limit);         // numero massimo di chunk
            return ps;
        }, (rs, rowNum) -> {
            Chunks chunk = new Chunks();
            chunk.setId(rs.getLong("id"));

            // Creiamo VideoChunks popolando anche titolo e fullContent
            VideoChunks videoChunks = new VideoChunks();
            videoChunks.setNumeroVideo(rs.getLong("numero_video_chunks")); // prima era setId
            videoChunks.setTitle(rs.getString("video_title"));
            videoChunks.setFullContent(rs.getString("video_full_content"));
            chunk.setVideoChunks(videoChunks);


            chunk.setChunkIndex(rs.getInt("chunk_index"));
            chunk.setContent(rs.getString("content"));
            // embedding non lo ricostruisco da DB (lo lasciamo null per evitare overhead)
            return chunk;
        });
    }


    /**
     * Salva un chunk nel database con embedding.
     */
    @Transactional
    public Chunks saveChunkJdbc(VideoChunks videoChunks, int chunkIndex, String content, Float[] embedding) throws Exception {

        PGobject pgVector = UtilsRepository.toPgVector(embedding);

        String sql = "INSERT INTO chunks (numero_video_chunks, chunk_index, content, embedding) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, videoChunks.getNumeroVideo());  // qui passiamo l'id di VideoChunks
            ps.setInt(2, chunkIndex);
            ps.setString(3, content);
            ps.setObject(4, pgVector);
            return ps;
        }, keyHolder);

        Chunks chunk = new Chunks();
        chunk.setId(keyHolder.getKey().longValue());
        chunk.setVideoChunks(videoChunks);  // assegniamo l'oggetto VideoChunks
        chunk.setChunkIndex(chunkIndex);
        chunk.setContent(content);
        chunk.setEmbedding(embedding);

        return chunk;
    }








}
