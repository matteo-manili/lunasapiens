package com.lunasapiens.service;

import ai.djl.translate.TranslateException;
import com.lunasapiens.Utils;
import com.lunasapiens.entity.ArticleContent;
import com.lunasapiens.repository.ArticleContentCustomRepositoryImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Servizio per gestire articoli e operazioni di embedding.
 *
 * 🔹 Scopo della classe:
 *    1️⃣ Calcolare embedding dei contenuti testuali.
 *    2️⃣ Salvare articoli nel database con embedding.
 *    3️⃣ Effettuare ricerche semantiche utilizzando gli embedding.
 */
@Service
public class ArticleSemanticService {

    @Autowired
    private ArticleContentCustomRepositoryImpl articleContentCustomRepository;


    @Autowired
    TextEmbeddingHuggingfaceService textEmbeddingHuggingfaceService;



    /**
     * Inserisce un articolo nel DB calcolando l'embedding.
     *
     * 🔹 Funzionamento:
     *    1️⃣ Calcola embedding tramite EmbeddingService.
     *    2️⃣ Salva l'articolo con il repository custom usando JDBC.
     *
     * @param content testo dell'articolo
     * @return ArticleContent salvato nel DB
     */
    public ArticleContent addArticle(String content) {
        try {
            // Calcola embedding tramite DJL
            //Float[] embedding = textEmbeddingService.computeCleanEmbedding(content);
            Float[] embedding = textEmbeddingHuggingfaceService.embedDocument( Utils.cleanHtmlText(content) );

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



    /**
     * Ricerca semantica basata su embedding, poi Full-Text Search.
     *
     * 🔹 Funzionamento:
     *    1️⃣ Calcola l'embedding della query.
     *    2️⃣ Cerca articoli nel DB ordinati per similarità embedding.
     *    3️⃣ Poi applica un filtro Full-Text Search.
     *
     * @param query testo della ricerca
     * @param limit numero massimo di risultati
     * @return lista di articoli ordinata per rilevanza
     */
    public List<ArticleContent> searchByEmbeddingThenFTS(String query, int limit) {
        try {
            //Float[] queryEmbedding = TextEmbeddingService.toFloatObjectArray(textEmbeddingService.predictor.predict(query));
            Float[] queryEmbedding = textEmbeddingHuggingfaceService.embedQuery(query);

            return articleContentCustomRepository.searchByEmbeddingThenFTS(queryEmbedding, query, limit);


        } catch (TranslateException e) {
            throw new RuntimeException("Errore nella predizione dell'embedding", e);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la query JDBC", e);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca semantica", e);
        }
    }


    /**
     * Ricerca semantica pura basata su embedding.
     *
     * 🔹 Funzionamento:
     *    1️⃣ Calcola embedding della query.
     *    2️⃣ Trova gli articoli più vicini nel database usando solo embedding.
     *
     * @param query testo della ricerca
     * @param limit numero massimo di risultati
     * @return lista di articoli ordinata per similarità
     */
    public List<ArticleContent> searchSemantic(String query, int limit) {
        try {
            //Float[] queryEmbedding = TextEmbeddingService.toFloatObjectArray(textEmbeddingService.predictor.predict(query));
            Float[] queryEmbedding = textEmbeddingHuggingfaceService.embedQuery( Utils.cleanHtmlText(query) );
            return articleContentCustomRepository.findNearestByEmbedding(queryEmbedding, limit);

        } catch (TranslateException e) {
            throw new RuntimeException("Errore nella predizione dell'embedding", e);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la query JDBC", e);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca semantica", e);
        }
    }





}
