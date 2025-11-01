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
    HuggingfaceTextEmbedding_E5LargeService textEmbeddingHuggingfaceService;



    public List<Chunks> findNearestChunksCosine(String query, int limit) {
        try {
            Float[] queryEmbedding = textEmbeddingHuggingfaceService.embedQuery(query);
            return chunksCustomRepository.findNearestChunksCosine(queryEmbedding, query, limit);

        } catch (TranslateException e) {
            throw new RuntimeException("Errore nella predizione dell'embedding", e);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la query JDBC", e);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca semantica", e);
        }
    }



    public List<Chunks> findNearestChunksWithFts(String query, int limit) {
        try {
            //Float[] queryEmbedding = TextEmbeddingService.toFloatObjectArray(textEmbeddingService.predictor.predict(query));
            Float[] queryEmbedding = textEmbeddingHuggingfaceService.embedQuery( query );

            return chunksCustomRepository.findNearestChunksWithFts(query, limit);
            //return chunksCustomRepository.findNearestChunksWithFtsCosine(queryEmbedding, query, limit);


        } catch (TranslateException e) {
            throw new RuntimeException("Errore nella predizione dell'embedding", e);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la query JDBC", e);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca semantica", e);
        }
    }




    public List<Chunks> findNearestChunksDistance(String query, int limit) {
        try {
            //Float[] queryEmbedding = TextEmbeddingService.toFloatObjectArray(textEmbeddingService.predictor.predict(query));
            Float[] queryEmbedding = textEmbeddingHuggingfaceService.embedQuery( query );

            return chunksCustomRepository.findNearestChunksDistance(queryEmbedding, limit);

        } catch (TranslateException e) {
            throw new RuntimeException("Errore nella predizione dell'embedding", e);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la query JDBC", e);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca semantica", e);
        }
    }





}
