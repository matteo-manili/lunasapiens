package com.lunasapiens.manualjobs.ArticleEmbedding;

import ai.djl.repository.MRL;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.translate.TranslateException;
import com.lunasapiens.entity.ArticleContent;
import com.lunasapiens.repository.ArticleContentCustomRepositoryImpl;
import com.lunasapiens.repository.ArticleContentRepository;
import com.lunasapiens.service.ArticleSemanticService;
import com.lunasapiens.manualjobs.ArticleEmbedding.service.TextEmbeddingService;
import com.lunasapiens.service.TextEmbeddingHuggingfaceService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ArticleEmbeddingManualJobs {


    @Autowired
    private ArticleContentRepository articleContentRepository;

    @Autowired
    private ArticleContentCustomRepositoryImpl articleContentCustomRepository;

    @Autowired
    TextEmbeddingHuggingfaceService textEmbeddingHuggingfaceService;

    @Autowired
    private ArticleSemanticService articleSemanticService;



    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    void testSearchSpecificWord() throws TranslateException {
        // Parola da cercare
        String query = "infezioni post-operatorie";

        // Effettua ricerca semantica
        List<ArticleContent> results = articleSemanticService.searchSemantic(query, 10);

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
                //Float[] embedding = textEmbeddingService.computeCleanEmbedding( article.getContent() );
                Float[] embedding = textEmbeddingHuggingfaceService.embedDocument( article.getContent() );

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
    void testSaveArticleWithEmbedding() throws Exception {
        // 1️⃣ Testo dell'articolo
        String content = "ciao mondoooooo";

        // 2️⃣ Salva l'articolo usando embeddingService, così l'embedding viene generato correttamente
        ArticleContent savedArticle = articleSemanticService.addArticle(content);

        // 3️⃣ Verifica che l'ID sia stato generato
        assertNotNull(savedArticle.getId(), "L'articolo dovrebbe avere un ID dopo il salvataggio");

        // 4️⃣ Recupera l'articolo dal database usando embedding appena generato
        Float[] embedding = savedArticle.getEmbedding();
        ArticleContent retrievedArticle = articleContentCustomRepository.findNearestByEmbedding(embedding, 1).get(0);

        // 5️⃣ Verifica che il contenuto corrisponda
        assertEquals(content, retrievedArticle.getContent(), "Il contenuto dell'articolo salvato dovrebbe corrispondere");

        // 6️⃣ Verifica che l'embedding corrisponda
        assertTrue(Arrays.equals(embedding, retrievedArticle.getEmbedding()),
                "L'embedding dell'articolo salvato dovrebbe corrispondere all'originale");
    }





    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    void testAddLargeArticleAndSearch() throws Exception {
        // 1️⃣ Genera un grande testo di esempio
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Questo è un testo di test lungo per l'articolo numero ").append(i).append(". ");
        }
        String largeContent = sb.toString();

        // 2️⃣ Inserisci articolo e calcola embedding
        ArticleContent savedArticle = articleSemanticService.addArticle(largeContent);

        assertNotNull(savedArticle.getId(), "L'articolo dovrebbe avere un ID dopo il salvataggio");
        assertNotNull(savedArticle.getEmbedding(), "L'embedding non dovrebbe essere nullo");
        assertTrue(savedArticle.getEmbedding().length > 0, "L'embedding dovrebbe avere dimensione maggiore di 0");

        // 3️⃣ Effettua ricerca semantica usando parte del testo
        String query = "articolo numero 500";
        List<ArticleContent> results = articleSemanticService.searchSemantic(query, 5);

        assertFalse(results.isEmpty(), "La ricerca dovrebbe restituire almeno un risultato");

        // 🔹 Stampa nel log i risultati trovati
        System.out.println("Risultati della ricerca per query: \"" + query + "\"");
        for (ArticleContent article : results) {
            System.out.println("ID: " + article.getId());
            System.out.println("Content snippet: " + article.getContent().substring(0, Math.min(100, article.getContent().length())) + "...");
            System.out.println("Embedding length: " + article.getEmbedding().length);
            System.out.println("--------------------------------------------------");
        }

        // 4️⃣ Verifica che il testo più simile sia presente
        boolean found = results.stream()
                .anyMatch(a -> a.getContent().contains("articolo numero 500"));
        assertTrue(found, "La ricerca semantica dovrebbe recuperare l'articolo corretto");
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
