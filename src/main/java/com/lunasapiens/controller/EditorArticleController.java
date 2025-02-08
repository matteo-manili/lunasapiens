package com.lunasapiens.controller;

import com.lunasapiens.Constants;
import com.lunasapiens.Utils;
import com.lunasapiens.entity.ArticleContent;
import com.lunasapiens.repository.ArticleContentRepository;
import com.lunasapiens.service.FileWithMetadata;
import com.lunasapiens.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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


/**
 * TODO
 * c'è un problema. Quando creo o modifico un articolo, se faccio l'upload di una immagine, questa rimane nel server S3 se decido
 * di non salvare più l'articolo.
 */
@Controller
public class EditorArticleController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(EditorArticleController.class);

    @Autowired
    private ArticleContentRepository articleContentRepository;

    @Autowired
    private S3Service s3Service;


    /**
     * Pagina pubblca
     * @param model
     * @return
     */
    @GetMapping("/blog")
    public String blog(Model model) {
        List<ArticleContent> articles = articleContentRepository.findAllByOrderByCreatedAtDesc(); // Recupera tutti gli articoli dal database
        model.addAttribute("articles", articles); // Aggiungi la lista degli articoli al modello
        return "blog";
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


    @GetMapping("/private/editorArticles")
    public String editor(Model model, RedirectAttributes redirectAttributes) {
        if (!isMatteoManilIdUser()) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Accesso negato: non hai i permessi per visualizzare questa pagina.");
            return "redirect:/error";
        }
        List<ArticleContent> articles = articleContentRepository.findAllByOrderByCreatedAtDesc(); // Recupera tutti gli articoli dal database
        model.addAttribute("articles", articles); // Aggiungi la lista degli articoli al modello
        return "private/editorArticles";
    }


    @PostMapping("/private/saveOrUpdateArticle")
    public String saveOrUpdateArticle(@RequestParam("id") Optional<Long> id,
                                      @RequestParam("content") String content, RedirectAttributes redirectAttributes) {
        if (!isMatteoManilIdUser()) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Accesso negato");
            return "redirect:/error";
        }
        try {
            ArticleContent articleContent;
            List<String> newImageNames = Utils.extractImageNames(content); // Estrai le immagini dal nuovo contenuto
            if (id.isPresent()) {
                // Aggiorna un articolo esistente
                articleContent = articleContentRepository.findById(id.get())
                        .orElseThrow(() -> new IllegalArgumentException("Articolo non trovato"));
                // Trova le immagini attualmente associate
                List<String> oldImageNames = Utils.extractImageNames(articleContent.getContent());
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
                articleContent = new ArticleContent();
            }
            // Aggiorna il contenuto dell'articolo
            articleContent.setContent(content);
            articleContentRepository.save(articleContent);
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
