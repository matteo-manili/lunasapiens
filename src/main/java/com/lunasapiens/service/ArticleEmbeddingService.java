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
public class ArticleEmbeddingService {

    @Autowired
    private ArticleContentCustomRepositoryImpl articleContentCustomRepository;

    private final ZooModel<String, float[]> model;
    private final Predictor<String, float[]> predictor;


    public static final String MODEL_PATH = "src/main/resources/models/multi-qa-MiniLM-L6-cos-v1";


    public ArticleEmbeddingService(ArticleContentRepository repository) {
        try {


            // FUNZIONA CON FILE (multi-qa-MiniLM-L6-cos-v1.pt) IN LOCALE MA PESANTE PER HEROKU FALLISCE IL DEPLOY, PRENDE TROPPA MEMORIA - ricerche semantiche ottime !!!
            // modello da 384 - creato custom con python dal "pytorch_model.bin" ho creto il "multi-qa-MiniLM-L6-cos-v1.pt"
            // per farlo vedere cartella C:\intellij_work\modello_minilm
            Criteria<String, float[]> criteria = Criteria.builder()
                    .optApplication(Application.NLP.TEXT_EMBEDDING)
                    .setTypes(String.class, float[].class)
                    .optEngine("PyTorch")
                    .optModelPath(Paths.get( MODEL_PATH ))
                    .optTranslator(new HFMinilmItalianTranslator())
                    .build();



            /*
            // FUNZIONA IN LOCALE MA PESANTE PER HEROKU, PRENDE TROPPA MEMORIA E DA ERRORE: Process running mem=1029M(201.0%) - Error R15 (Memory quota vastly exceeded)
            // - Stopping process with SIGKILL - RICERCHE SEMANTICHE QUASI BUONE
            // modello da 768
            // 1️⃣ Definisci criteri per caricare il modello di embedding da Hugging Face online, utilizza il modello di default, solo inglese
            Criteria<String, float[]> criteria = Criteria.builder()
                    .optApplication(Application.NLP.TEXT_EMBEDDING)
                    .setTypes(String.class, float[].class)
                    .optEngine("PyTorch") // Assicura uso PyTorch
                    .build();
             */


            // modello da 384 OK
            /*
            Criteria<String, float[]> criteria = Criteria.builder()
                    .optApplication(Application.NLP.TEXT_EMBEDDING)
                    .setTypes(String.class, float[].class)
                    .optEngine("PyTorch")

                    // FUNZIONA IN LOCALE MA PESANTE PER HEROKU FALLISCE IL DEPLOY, PRENDE TROPPA MEMORIA - RICERCHE SEMANTICHE QUASI BUONE
                    //.optModelUrls("djl://ai.djl.huggingface.pytorch/sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2")

                    // FUNZIONA IN LOCALE E SU HEROKU - RICERCHE DANNO NON BUONO RISULTATI - OK !!!!
                    .optModelUrls("djl://ai.djl.huggingface.pytorch/sentence-transformers/all-MiniLM-L6-v2") // FUNZIONA GIRA SU HEROKU - RICERCHE SEMANTICHE PESSIME

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
            Float[] embedding = cleanTextEmbeddingPredictor(content);
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
    public List<ArticleContent> searchSemantic(String query, int limit) {
        try {
            Float[] queryEmbedding = Utils.toFloatObjectArray(predictor.predict(query));
            return articleContentCustomRepository.findNearestByEmbedding(queryEmbedding, limit);

        } catch (TranslateException e) {
            throw new RuntimeException("Errore nella predizione dell'embedding", e);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la query JDBC", e);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca semantica", e);
        }
    }


    public Float[] cleanTextEmbeddingPredictor(String content)  {
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
