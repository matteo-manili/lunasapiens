package com.lunasapiens;


import com.lunasapiens.entity.ArticleContent;
import com.lunasapiens.repository.ArticleContentCustomRepositoryImpl;
import com.lunasapiens.service.ArticleEmbeddingService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class LunasapiensApplicationTests {

    @Autowired
    private ArticleEmbeddingService articleEmbeddingService;

    @Autowired
    private ArticleContentCustomRepositoryImpl articleContentCustomRepository;


    @Test
    void contextLoads() {
    }


    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    void testSaveArticleWithEmbedding() throws Exception {
        // 1️⃣ Testo dell'articolo
        String content = "ciao mondoooooo";

        // 2️⃣ Salva l'articolo usando embeddingService, così l'embedding viene generato correttamente
        ArticleContent savedArticle = articleEmbeddingService.addArticle(content);

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
        ArticleContent savedArticle = articleEmbeddingService.addArticle(largeContent);

        assertNotNull(savedArticle.getId(), "L'articolo dovrebbe avere un ID dopo il salvataggio");
        assertNotNull(savedArticle.getEmbedding(), "L'embedding non dovrebbe essere nullo");
        assertTrue(savedArticle.getEmbedding().length > 0, "L'embedding dovrebbe avere dimensione maggiore di 0");

        // 3️⃣ Effettua ricerca semantica usando parte del testo
        String query = "articolo numero 500";
        List<ArticleContent> results = articleEmbeddingService.searchSemantic(query, 5);

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




}
