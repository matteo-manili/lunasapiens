package com.lunasapiens.manualjobs.blogRandomWiki;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunasapiens.aiModels.huggngface.HuggingfaceLLaMASummarizationService;
import com.lunasapiens.aiModels.huggngface.HuggingfaceTextEmbedding_E5LargeService;
import com.lunasapiens.entity.Chunks;
import com.lunasapiens.entity.VideoChunks;
import com.lunasapiens.manualjobs.SpeechToChunks.service.AudioTranscriptionMultiThredService;
import com.lunasapiens.manualjobs.SpeechToChunks.service.FaiSintesiIAService;
import com.lunasapiens.manualjobs.SpeechToChunks.service.PunteggiaturaIAService;
import com.lunasapiens.repository.ChunksCustomRepositoryImpl;
import com.lunasapiens.repository.VideoChunksRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;


@SpringBootTest
class BlogRandomWikiManualJob {

    @Autowired
    private AudioTranscriptionMultiThredService audioTranscriptionMultiThredService;

    @Autowired
    private PunteggiaturaIAService punteggiaturaIAService;

    @Autowired
    private FaiSintesiIAService faiSintesiIAService;

    @Autowired
    private HuggingfaceTextEmbedding_E5LargeService textEmbedding;

    @Autowired
    private HuggingfaceLLaMASummarizationService summarizationService;

    @Autowired
    private VideoChunksRepository videoChunksRepository;

    @Autowired
    private ChunksCustomRepositoryImpl chunksCustomRepository;

    @Autowired
    private RestTemplate restTemplate;


    @Test
    //@Disabled("Disabilitato temporaneamente per debug")
    public void prendiHtmlPageWiki() throws Exception {



        // Endpoint Wikipedia in italiano
        String baseUrl = "https://it.wikipedia.org/w/api.php";
        // Costruzione URL con parametri (tutti commentati per scelta manuale)
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                // ===== PARAMETRI BASE OBBLIGATORI =====
                .queryParam("action", "query")          // Azione API
                .queryParam("format", "json")           // Output JSON
                // ===== RANDOM PAGE =====
                .queryParam("generator", "random")      // Genera pagina casuale
                .queryParam("grnnamespace", 0)          // SOLO articoli enciclopedici (fondamentale)
                .queryParam("grnlimit", 1)              // Numero di pagine casuali
                // ===== CONTENUTO =====
                .queryParam("prop", "extracts")         // Richiede il testo della pagina
                .queryParam("explaintext", 1)           // Testo puro (NO HTML)
                .queryParam("grnminsize", 10000)         // MINIMO DIMENSIONE PAGINA - Evita voci troppo corte
                //.queryParam("exchars", 100000)           // LIMITE LUNGHEZZA TESTO - Max caratteri restituiti // tronca il testo

                // ===== OPZIONALI (ATTIVA SOLO SE SERVE) =====
                // .queryParam("exintro", 1) // ---- SOLO INTRODUZIONE (lead) ----
                // .queryParam("exsectionformat", "plain") // plain / wiki / raw // ---- FORMATO ESTRATTO ----
                // .queryParam("redirects", 1)          // Segue redirect automaticamente // ---- REDIRECT ----
                // .queryParam("prop", "extracts|info") // ---- METADATI PAGINA (se servono) ----
                // .queryParam("inprop", "url")         // Include URL canonico // ---- METADATI PAGINA (se servono) ----
                // .queryParam("continue", ""); // ---- CONTINUA QUERY (per batch) ----
                ;

        String finalUrl = builder.toUriString();
        // ===== HEADERS OBBLIGATORI (WIKIMEDIA) =====
        HttpHeaders headers = new HttpHeaders();
        headers.set(
                "User-Agent",
                "BlogRandomWikiBot/1.0 (https://lunasapiens.com; dev@lunasapiens.com)"
        );
        headers.set("Accept", "application/json");
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        // ===== CHIAMATA HTTP =====
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                finalUrl,
                HttpMethod.GET,
                entity,
                String.class
        );
        String response = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();

        // Parse JSON
        JsonNode root = mapper.readTree(response);
        // Naviga fino a query.pages
        JsonNode pagesNode = root
                .path("query")
                .path("pages");
        // Prende la PRIMA (e unica) pagina casuale
        Iterator<JsonNode> pagesIterator = pagesNode.elements();
        if (!pagesIterator.hasNext()) {
            throw new RuntimeException("Nessuna pagina trovata");
        }
        JsonNode pageNode = pagesIterator.next();
        // Estrai titolo e testo
        String titolo = pageNode.path("title").asText();
        String testo = pageNode.path("extract").asText();

        System.out.println("\n===== RISULTATO PARSATO =====");
        System.out.println("TITOLO:\n" + titolo);
        System.out.println("\nTESTO:\n" + testo);
    }




}
