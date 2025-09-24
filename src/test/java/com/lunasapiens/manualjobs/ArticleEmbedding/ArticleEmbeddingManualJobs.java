package com.lunasapiens.manualjobs.ArticleEmbedding;


import ai.djl.repository.MRL;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.translate.TranslateException;
import com.lunasapiens.entity.ArticleContent;
import com.lunasapiens.repository.ArticleContentCustomRepositoryImpl;
import com.lunasapiens.repository.ArticleContentRepository;
import com.lunasapiens.service.ArticleEmbeddingService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ArticleEmbeddingManualJobs {


    @Autowired
    private ArticleContentRepository articleContentRepository;

    @Autowired
    private ArticleContentCustomRepositoryImpl articleContentCustomRepository;

    @Autowired
    private ArticleEmbeddingService articleEmbeddingService;



    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    void testSearchSpecificWord() throws TranslateException {
        // Parola da cercare
        String query = "infezioni post-operatorie";

        // Effettua ricerca semantica
        List<ArticleContent> results = articleEmbeddingService.searchSemantic(query, 10);

        // Controlla che ci siano risultati
        assertFalse(results.isEmpty(), "La ricerca dovrebbe restituire almeno un risultato");

        // Stampa i risultati nel log, gestendo embedding null
        System.out.println("Risultati della ricerca per parola: \"" + query + "\"");
        for (ArticleContent article : results) {
            System.out.println("ID: " + article.getId());
            System.out.println("Content snippet: " +
                    article.getContent().substring(0, Math.min(100, article.getContent().length())) + "...");
            // Gestione embedding null
            if (article.getEmbedding() != null) {
                System.out.println("Embedding length: " + article.getEmbedding().length);
            } else {
                System.out.println("Embedding: null");
            }
            System.out.println("--------------------------------------------------");
        }

        // Verifica che almeno un articolo contenga la parola cercata
        boolean found = results.stream()
                .anyMatch(a -> a.getContent().toLowerCase().contains(query.toLowerCase()));
        assertTrue(found, "La ricerca dovrebbe recuperare almeno un articolo contenente la parola cercata");
    }




    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    void populateEmbeddingsForExistingArticles() throws Exception {
        List<ArticleContent> articles = articleContentRepository.findAllByOrderByCreatedAtDesc();
        for (ArticleContent article : articles) {
            try {
                Float[] embedding = articleEmbeddingService.cleanTextEmbeddingPredictor( article.getContent() );
                System.out.println("Dimensione embedding: " + embedding.length);
                ArticleContent articleContentRefresh = articleContentCustomRepository.updateArticleEmbeddingJdbc(article.getId(), embedding);
                System.out.println("Aggiornato embedding articolo ID: " + articleContentRefresh.getId());

            } catch (Exception e) {
                System.err.println("Errore popolamento campo embedding articolo ID: " + article.getId());
                e.printStackTrace();
            }
        }
    }


    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    void listAvailableEmbeddingModels() {
        ModelZoo.listModels().forEach((app, mrlList) -> {
            System.out.println("=== Application: " + app + " ===");
            for (MRL mrl : mrlList) {
                System.out.println("GroupId:    " + mrl.getGroupId());
                System.out.println("ArtifactId: " + mrl.getArtifactId());
                System.out.println("Version:    " + mrl.getVersion());
                System.out.println("Repository: " + mrl.getRepository());
                System.out.println("Artifact URIs: ");
                System.out.println("---------------------------");
            }
        });
    }




}
