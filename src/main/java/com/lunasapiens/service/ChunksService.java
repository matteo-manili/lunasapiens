package com.lunasapiens.service;

import ai.djl.translate.TranslateException;
import com.lunasapiens.entity.Chunks;
import com.lunasapiens.repository.ChunksCustomRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;


@Service
public class ChunksService {

    @Autowired
    private ChunksCustomRepositoryImpl chunksCustomRepository;

    @Autowired
    TextEmbeddingService textEmbeddingService;


    public List<Chunks> findNearestChunksWithFts(String query, int limit) {
        try {
            Float[] queryEmbedding = TextEmbeddingService.toFloatObjectArray(textEmbeddingService.predictor.predict(query));


            //return chunksCustomRepository.findNearestChunksFtsThenCosine(queryEmbedding, query, limit);
            //return chunksCustomRepository.findNearestChunksWithFts(query, limit);
            return chunksCustomRepository.findNearestChunksWithFtsCosine(queryEmbedding, query, limit);




        } catch (TranslateException e) {
            throw new RuntimeException("Errore nella predizione dell'embedding", e);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la query JDBC", e);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca semantica", e);
        }
    }


    public List<Chunks> findNearestChunks(String query, int limit) {
        try {
            Float[] queryEmbedding = TextEmbeddingService.toFloatObjectArray(textEmbeddingService.predictor.predict(query));
            return chunksCustomRepository.findNearestChunks(queryEmbedding, limit);

        } catch (TranslateException e) {
            throw new RuntimeException("Errore nella predizione dell'embedding", e);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la query JDBC", e);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca semantica", e);
        }
    }





}
