package com.lunasapiens.service;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import com.lunasapiens.entity.ArticleContent;
import com.lunasapiens.repository.ArticleContentCustomRepositoryImpl;
import com.lunasapiens.repository.ArticleContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

@Service
public class EmbeddingService {

    @Autowired
    private ArticleContentCustomRepositoryImpl articleContentCustomRepository;




    private final ZooModel<String, float[]> model;
    private final Predictor<String, float[]> predictor;

    public EmbeddingService(ArticleContentRepository repository) {
        try {

            // modello da 768
            // 1️⃣ Definisci criteri per caricare il modello di embedding da Hugging Face online

            Criteria<String, float[]> criteria = Criteria.builder()
                    .optApplication(Application.NLP.TEXT_EMBEDDING)
                    .setTypes(String.class, float[].class)
                    .optEngine("PyTorch") // Assicura uso PyTorch
                    .build();


            // 2️⃣ Carica modello dal ModelZoo (scarica automaticamente online)
            model = ModelZoo.loadModel(criteria);

            // 3️⃣ Crea il predictor
            predictor = model.newPredictor();

        } catch (IOException | ModelException e) {
            throw new RuntimeException("Errore caricando il modello di embedding online", e);
        }
    }




    // Inserisce un articolo calcolando l'embedding
    public ArticleContent addArticle(String content) {
        try {
            // Calcola embedding tramite DJL
            float[] embedding = predictor.predict(content);

            // Salva usando il repository custom con JDBC
            return articleContentCustomRepository.saveArticleWithEmbeddingJdbc(content, embedding);

        } catch (TranslateException | SQLException e) {
            throw new RuntimeException("Errore creando o salvando l'articolo con embedding", e);
        }
    }


    // Ricerca semantica basata su embedding
    public List<ArticleContent> searchSimilar(String query, int limit) {
        try {
            float[] queryEmbedding = predictor.predict(query);
            return articleContentCustomRepository.findNearestJdbc(queryEmbedding, limit);
        } catch (TranslateException | SQLException e) {
            throw new RuntimeException("Errore durante la ricerca semantica", e);
        }
    }







}
