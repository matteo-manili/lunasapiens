package com.lunasapiens.service;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import com.lunasapiens.Utils;
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


    public static final String MODEL_PATH = "src/main/resources/models/multi-qa-MiniLM-L6-cos-v1";


    public EmbeddingService(ArticleContentRepository repository) {
        try {
            // modello da 768
            // 1️⃣ Definisci criteri per caricare il modello di embedding da Hugging Face online, utilizza quello standard, solo inglese
            /*
            Criteria<String, float[]> criteria = Criteria.builder()
                    .optApplication(Application.NLP.TEXT_EMBEDDING)
                    .setTypes(String.class, float[].class)
                    .optEngine("PyTorch") // Assicura uso PyTorch
                    .build();
             */

            // modello da 384
            // Criteri per caricare il modello Hugging Face specifico per Multilanguage, anche italiano
            Criteria<String, float[]> criteria = Criteria.builder()
                    .optApplication(Application.NLP.TEXT_EMBEDDING)
                    .setTypes(String.class, float[].class)
                    .optEngine("PyTorch")
                    .optModelPath(Paths.get( MODEL_PATH ))
                    .optTranslator(new HFMinilmItalianTranslator())
                    .build();

            /* // FUNZIONA
            Criteria<String, float[]> criteria = Criteria.builder()
                    .optApplication(Application.NLP.TEXT_EMBEDDING)
                    .setTypes(String.class, float[].class)
                    .optEngine("PyTorch")
                    .optModelUrls("djl://ai.djl.huggingface.pytorch/sentence-transformers/all-MiniLM-L6-v2")
                    .build();
             */


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
            Float[] embedding = Utils.toFloatObjectArray( predictor.predict(Utils.cleanText(content)) );
            // Salva usando il repository custom con JDBC
            return articleContentCustomRepository.saveArticleWithEmbeddingJdbc(content, embedding);

        } catch (TranslateException e) {
            throw new RuntimeException("Errore nella predizione dell'embedding", e);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la query JDBC", e);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca semantica", e);
        }
    }


    // Ricerca semantica basata su embedding
    public List<ArticleContent> searchSimilar(String query, int limit) {
        try {
            Float[] queryEmbedding = Utils.toFloatObjectArray(predictor.predict(query));
            return articleContentCustomRepository.findNearestJdbc(queryEmbedding, limit);

        } catch (TranslateException e) {
            throw new RuntimeException("Errore nella predizione dell'embedding", e);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la query JDBC", e);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca semantica", e);
        }
    }


    public Float[] embeddingPredictor(String content)  {
        try {
            String text = Utils.cleanText(content);
            System.out.println(text);

            return Utils.toFloatObjectArray( predictor.predict( text ) );
        } catch (TranslateException e) {
            e.printStackTrace();
            return null;
        }
    }







}
