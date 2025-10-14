package com.lunasapiens.repository;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class ChunksCustomRepositoryImpl implements ChunksCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ChunksCustomRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



    public List<Chunks> findNearestChunksWithFts(String userQuestion, int limit) throws Exception {

            String sql =
                    "SELECT c.id, c.numero_video_chunks, c.chunk_index, c.content, " +
                            "       vc.title AS video_title, vc.full_content AS video_full_content, vc.metadati, " +
                            "       ts_rank(to_tsvector('italian', c.content), plainto_tsquery('italian', ?)) AS fts_rank " +
                            "FROM chunks c " +
                            "JOIN video_chunks vc ON c.numero_video_chunks = vc.numero_video " +
                            "ORDER BY fts_rank DESC " +
                            "LIMIT ?";

            ObjectMapper mapper = new ObjectMapper();

            return jdbcTemplate.query(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, userQuestion);  // testo della domanda per FTS
                ps.setInt(2, limit);             // numero massimo di chunk
                return ps;
            }, (rs, rowNum) -> {
                Chunks chunk = new Chunks();
                chunk.setId(rs.getLong("id"));

                VideoChunks videoChunks = new VideoChunks();
                videoChunks.setNumeroVideo(rs.getLong("numero_video_chunks"));
                videoChunks.setTitle(rs.getString("video_title"));
                videoChunks.setFullContent(rs.getString("video_full_content"));
                chunk.setVideoChunks(videoChunks);

                // 🔥 Conversione sicura da JSON → Map
                String json = rs.getString("metadati");
                if (json != null && !json.isBlank()) {
                    try {
                        Map<String, Object> metaMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
                        videoChunks.setMetadati(metaMap);
                    } catch (Exception e) {
                        System.err.println("⚠️ Errore parsing metadati JSON: " + e.getMessage());
                        videoChunks.setMetadati(Map.of()); // fallback a {}
                    }
                } else {
                    videoChunks.setMetadati(Map.of()); // JSON vuoto
                }

                chunk.setChunkIndex(rs.getInt("chunk_index"));
                chunk.setContent(rs.getString("content"));

                // embedding non utilizzato
                return chunk;
            });
        }


    public List<Chunks> findNearestChunksCosine(Float[] embedding, String userQuestion, int limit) throws Exception {

        PGobject pgVector = UtilsRepository.toPgVector(embedding);

        String sql = "SELECT c.id, c.numero_video_chunks, c.chunk_index, c.content, " +
                        "       vc.title AS video_title, vc.full_content AS video_full_content, " +
                        "       c.embedding <=> ? AS cosine_distance " +
                        "FROM chunks c " +
                        "JOIN video_chunks vc ON c.numero_video_chunks = vc.numero_video " +
                        "ORDER BY cosine_distance ASC " + // più vicino = più simile
                        "LIMIT ?";

        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, pgVector);                       // embedding utente
            ps.setInt(2, limit);                             // numero massimo di chunk
            return ps;
        }, (rs, rowNum) -> {
            Chunks chunk = new Chunks();
            chunk.setId(rs.getLong("id"));

            VideoChunks videoChunks = new VideoChunks();
            videoChunks.setNumeroVideo(rs.getLong("numero_video_chunks"));
            videoChunks.setTitle(rs.getString("video_title"));
            videoChunks.setFullContent(rs.getString("video_full_content"));
            chunk.setVideoChunks(videoChunks);

            chunk.setChunkIndex(rs.getInt("chunk_index"));
            chunk.setContent(rs.getString("content"));

            return chunk;
        });
    }





    public List<Chunks> findNearestChunksWithFtsCosine(Float[] embedding, String userQuestion, int limit) throws Exception {
    PGobject pgVector = UtilsRepository.toPgVector(embedding);

        String sql =
                "WITH nearest AS ( " +
                        "   SELECT c.id, c.numero_video_chunks, c.chunk_index, c.content, c.embedding, " +
                        "          vc.title AS video_title, vc.full_content AS video_full_content, vc.metadati, " +
                        "          c.embedding <=> ? AS cosine_distance " +
                        "   FROM chunks c " +
                        "   JOIN video_chunks vc ON c.numero_video_chunks = vc.numero_video " +
                        "   ORDER BY cosine_distance " +
                        "   LIMIT 150 " +
                        ") " +
                        "SELECT id, numero_video_chunks, chunk_index, content, embedding, video_title, video_full_content, metadati, " +
                        "       ts_rank(to_tsvector('italian', content), plainto_tsquery('italian', ?)) AS fts_rank, " +
                        "       cosine_distance " +
                        "FROM nearest " +
                        "ORDER BY fts_rank DESC, cosine_distance ASC " +
                        "LIMIT ?";


        ObjectMapper mapper = new ObjectMapper();

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

            // 🔥 Conversione sicura da JSON → Map
            String json = rs.getString("metadati");
            if (json != null && !json.isBlank()) {
                try {
                    Map<String, Object> metaMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
                    videoChunks.setMetadati(metaMap);
                } catch (Exception e) {
                    System.err.println("⚠️ Errore parsing metadati JSON: " + e.getMessage());
                    videoChunks.setMetadati(Map.of()); // fallback a {}
                }
            } else {
                videoChunks.setMetadati(Map.of()); // JSON vuoto
            }

            chunk.setChunkIndex(rs.getInt("chunk_index"));
            chunk.setContent(rs.getString("content"));
            // embedding non lo ricostruisco da DB (lo lasciamo null per evitare overhead)
            return chunk;
        });

    }




    public List<Chunks> findNearestChunksDistance(Float[] embedding, int limit) throws Exception {
        PGobject pgVector = UtilsRepository.toPgVector(embedding);

        String sql =
                "SELECT c.id, c.numero_video_chunks, c.chunk_index, c.content, c.embedding, " +
                        "       vc.title AS video_title, vc.full_content AS video_full_content " +
                        "FROM chunks c " +
                        "JOIN video_chunks vc ON c.numero_video_chunks = vc.numero_video " +
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
