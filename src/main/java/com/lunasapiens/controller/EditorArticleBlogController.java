package com.lunasapiens.controller;

import com.lunasapiens.Constants;
import com.lunasapiens.utils.Utils;
import com.lunasapiens.entity.ArticleContent;
import com.lunasapiens.repository.ArticleContentCustomRepositoryImpl;
import com.lunasapiens.repository.ArticleContentRepository;
import com.lunasapiens.service.ArticleSemanticService;
import com.lunasapiens.aiModels.huggngface.HuggingfaceTextEmbedding_E5LargeService;
import com.lunasapiens.service.FileWithMetadata;
import com.lunasapiens.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
public class EditorArticleBlogController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(EditorArticleBlogController.class);

    @Autowired
    private ArticleContentRepository articleContentRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    HuggingfaceTextEmbedding_E5LargeService textEmbeddingHuggingfaceService;

    @Autowired
    private ArticleSemanticService articleSemanticService;

    @Autowired
    private ArticleContentCustomRepositoryImpl articleContentCustomRepository;

    /**
     * Pagina pubblca
     * @param model
     * @return
     */
    @GetMapping("/blog")
    public String blog(@RequestParam(name = "page", defaultValue = "0") String pageParam,
                       @RequestParam(name = "search", required = false) String search,
                       Model model) {


        if (search != null && !search.isBlank()) {
            // 🔹 Ricerca semantica
            //List<ArticleContent> results = articleSemanticService.searchByEmbedding(search, 10); // massimo 10 risultati
            // 🔹 Ricerca semantica e FTS
            List<ArticleContent> results = articleSemanticService.searchByEmbeddingThenFTS(search, 10); // 10 risultati max
            // RICERCA FTS
            //List<ArticleContent> results = articleContentCustomRepository.searchByKeywordFTS(search, 10); // massimo 10 risultati

            setModelAttributeArticlesPage(results, model, search);
            return "blog";
        }

        // 🔹 Paginazione normale
        int page = parsePositivePage(pageParam);
        loadPagedArticles(page, model);
        return "blog";
    }


    @GetMapping("/private/editorArticles")
    public String editor(@RequestParam(name = "page", defaultValue = "0") String pageParam,
                         @RequestParam(name = "search", required = false) String search,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (!isMatteoManilIdUser()) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Accesso negato: non hai i permessi per visualizzare questa pagina.");
            return "redirect:/error";
        }

        if (search != null && !search.isBlank()) {
            // 🔹 Ricerca semantica
            //List<ArticleContent> results = articleSemanticService.searchByEmbedding(search, 10); // massimo 10 risultati
            // 🔹 Ricerca semantica e FTS
            List<ArticleContent> results = articleSemanticService.searchByEmbeddingThenFTS(search, 10); // 10 risultati max
            // RICERCA FTS
            //List<ArticleContent> results = articleContentCustomRepository.searchByKeywordFTS(search, 10); // massimo 10 risultati

            // Avvolgi in una Page fake
            setModelAttributeArticlesPage(results, model, search);
            return "private/editorArticles";
        }

        int page = parsePositivePage(pageParam);
        loadPagedArticles(page, model);
        return "private/editorArticles";
    }


    private void setModelAttributeArticlesPage(List<ArticleContent> results, Model model, String search){
        Page<ArticleContent> page = new PageImpl<>(results, Pageable.unpaged(), results.size());
        model.addAttribute("articlePage", page);
        model.addAttribute("hideSearch", true);  // utile se vuoi nascondere la paginazione
        model.addAttribute("search", search);
        model.addAttribute("currentPage", 0); // ← aggiungi questa riga

    }





    private void loadPagedArticles(int page, Model model) {
        int pageSize = 10;
        Page<ArticleContent> articlePage = articleContentRepository.findAll(PageRequest.of(page, pageSize, Sort.by("createdAt").descending()));
        model.addAttribute("articlePage", articlePage);
        model.addAttribute("currentPage", page);
    }



    @PostMapping("/private/saveOrUpdateArticle")
    public String saveOrUpdateArticle(@RequestParam("id") Optional<Long> id,
                                      @RequestParam("content") String content, RedirectAttributes redirectAttributes) {
        if (!isMatteoManilIdUser()) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Accesso negato");
            return "redirect:/error";
        }
        try {
            ArticleContent article;
            List<String> newImageNames = Utils.extractImageNames(content); // Estrai le immagini dal nuovo contenuto
            if (id.isPresent()) {
                // Aggiorna un articolo esistente
                article = articleContentRepository.findById(id.get())
                        .orElseThrow(() -> new IllegalArgumentException("Articolo non trovato"));
                // Trova le immagini attualmente associate
                List<String> oldImageNames = Utils.extractImageNames(article.getContent());
                // Trova le immagini da eliminare (presenti prima ma non più utilizzate)
                List<String> imagesToDelete = new ArrayList<>(oldImageNames);
                imagesToDelete.removeAll(newImageNames); // Rimuovi le immagini ancora presenti nel nuovo contenuto
                // Elimina le immagini non più utilizzate
                for( String imgDelete : imagesToDelete ){
                    try{
                        Optional<FileWithMetadata> imageOptional = Optional.ofNullable(s3Service.getImageFromS3(imgDelete));
                        if(imageOptional.isPresent()){
                            s3Service.deleteFile(imgDelete);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // Crea un nuovo articolo, MA NON impostare manualmente l'ID
                article = new ArticleContent();
                articleContentRepository.updateSequence();
            }
            // Aggiorna il contenuto dell'articolo
            article.setContent(content);
            ArticleContent articleSave = articleContentRepository.save(article);

            System.out.println( "id: "+id );
            System.out.println( "articleSave.getId(): "+articleSave.getId() );

            // aggiorno la colonna embedding
            //Float[] embedding = textEmbeddingService.computeCleanEmbedding( articleSave.getContent() );
            Float[] embedding = textEmbeddingHuggingfaceService.embedDocument( Utils.cleanHtmlText(articleSave.getContent()) );

            System.out.println("Dimensione embedding: " + embedding.length);
            ArticleContent articleContentRefresh = articleContentCustomRepository.updateArticleEmbeddingJdbc(articleSave.getId(), embedding);
            System.out.println("Aggiornato embedding articolo ID: " + articleContentRefresh.getId());


            redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Articolo salvato con successo!");
            return "redirect:/private/editorArticles";

        } catch (Exception e) {
            logger.error("Errore durante il salvataggio dell'articolo", e);
            redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Errore durante il salvataggio dell'articolo");
            return "redirect:/private/editorArticles";
        }
    }


    /**
     * endpoint per restituire i dettagli di un articolo per l'editor.
     */
    @GetMapping("/private/article/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getArticle(@PathVariable Long id) {
        if (!isMatteoManilIdUser()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Accesso negato"));
        }
        Optional<ArticleContent> article = articleContentRepository.findById(id);
        if (article.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Articolo non trovato"));
        }
        return ResponseEntity.ok(Map.of("id", article.get().getId(), "content", article.get().getContent()));
    }


    @DeleteMapping("/private/deleteArticle/{id}")
    public String deleteArticle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!isMatteoManilIdUser()) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Accesso negato");
            return "redirect:/error";
        }
        logger.info("Cancello articolo con ID: " + id);
        // Trova l'articolo
        articleContentRepository.findById(id).ifPresent(article -> {
            // Estrai i codici immagine dal contenuto
            List<String> imagesNames = Utils.extractImageNames(article.getContent());
            logger.info("Nomi immagine trovati nell'articolo: " + imagesNames);
            for(String imgDelete : imagesNames){
                try {
                    Optional<FileWithMetadata> imageOptional = Optional.ofNullable(s3Service.getImageFromS3(imgDelete));
                    if (imageOptional.isPresent()) {
                        s3Service.deleteFile(imgDelete);
                        logger.info("Immagine eliminata: " + imgDelete);
                    }
                } catch (Exception e) {
                    logger.error("Errore nell'eliminazione dell'immagine: " + imgDelete + " - " + e.getMessage(), e);
                }
            }
            // Cancella l'articolo
            articleContentRepository.delete(article);
            logger.info("Articolo cancellato con successo.");
        });
        redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Articolo e immagini cancellati con successo!");
        return "redirect:/private/editorArticles";
    }


    /**
     * private/upload-image-article
     */
    @PostMapping("/private/" + Constants.DOM_LUNA_SAPIENS_UPLOAD_IMAGE_ARTICLE)
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("upload") MultipartFile file) {
        if (!isMatteoManilIdUser()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Accesso negato"));
        }
        try {
            logger.info("Sono in upload-image-article");
            // Validazione del tipo MIME
            if (!file.getContentType().startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Formato file non supportato. Carica un'immagine valida."));
            }
            // Validazione della dimensione del file (massimo 6 MB)
            if (file.getSize() > 6_000_000) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "File troppo grande. La dimensione massima è 6MB."));
            }
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
            // Genera una parte randomica di 3 caratteri (mix di lettere e numeri) usando UUID
            String randomString = UUID.randomUUID().toString().substring(0, 3);  // Prendi i primi 3 caratteri
            // Trova l'estensione del file originale
            int lastDotIndex = file.getOriginalFilename().lastIndexOf(".");
            String extension = (lastDotIndex != -1) ? file.getOriginalFilename().substring(lastDotIndex) : "";
            // Crea il nuovo nome file con il timestamp
            String fileName = "image_" + timestamp + "_" + randomString + extension;
            s3Service.uploadFile(fileName, file.getInputStream());
            // Creazione dell'URL per recuperare l'immagine
            String imageUrl = "/" + Constants.DOM_LUNA_SAPIENS_IMAGES_ARTICLE + "/" + fileName;
            // Risposta con l'URL per CKEditor
            Map<String, Object> response = new HashMap<>();
            response.put("url", imageUrl);
            response.put("default", imageUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Errore durante l'upload dell'immagine", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Si è verificato un errore durante il caricamento dell'immagine."));
        }
    }


    /**
     * Retrive Image Public e in Cache
     */
    @Cacheable(value = Constants.IMAGES_ARTICLE_CACHE, key = "#nameImage")
    @GetMapping("/"+Constants.DOM_LUNA_SAPIENS_IMAGES_ARTICLE+"/{nameImage}")
    public ResponseEntity<byte[]> getImage(@PathVariable String nameImage) {
        logger.info("sono in "+Constants.DOM_LUNA_SAPIENS_IMAGES_ARTICLE+"/{nameImage}");
        try {
            // Recupera i byte dell'immagine dal bucket S3
            FileWithMetadata fileWithMetadata = s3Service.getImageFromS3(nameImage);
            return ResponseEntity.ok()
                    .header("Content-Type", fileWithMetadata.getContentType())
                    .body(fileWithMetadata.getData());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }



    /************** NON USO ******************* */
    /************** NON USO ******************* */
    /************** NON USO ******************* */
    /************** NON USO ******************* */

    /**
     * Endpoint per il download di un file direttamente

    @GetMapping("/s3-download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
        try {
            // Recupera il contenuto del file dal bucket S3
            InputStreamResource resource = s3Service.downloadFile(fileName);

            // Restituisci il file con il giusto Content-Type
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)  // O altre tipologie se conosci il tipo di file
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
     */

    /**
     * Endpoint per ottenere l'URL presigned per il download di un file
     * sarebbe la creazione di un url che ha una validità temporanea definita e che è usato per scaricare un'immagine (o un file)
     * del bucket dall'esterno senza autenticazione.

    @GetMapping("/s3-generate-presigned-url/{fileName}")
    public ResponseEntity<String> generatePresignedUrl(@PathVariable String fileName) {
        try {
            // Genera l'URL presigned per il download del file
            URL presignedUrl = s3Service.generatePresignedUrl(fileName, Duration.ofMinutes(15));

            if (presignedUrl != null) {
                return ResponseEntity.ok(presignedUrl.toString());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Errore nella generazione dell'URL presigned");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore interno: " + e.getMessage());
        }
    }
     */


}
