package com.lunasapiens.controller;


import com.lunasapiens.Constants;
import com.lunasapiens.entity.ArticleContent;
import com.lunasapiens.entity.ArticleImage;
import com.lunasapiens.repository.ArticleContentRepository;
import com.lunasapiens.repository.ArticleImageRepository;
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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
public class ArticleController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticleContentRepository articleContentRepository;

    @Autowired
    private ArticleImageRepository articleImageRepository;



    @GetMapping("/private/editor")
    public String editor(Model model) {
        List<ArticleContent> articles = articleContentRepository.findAllByOrderByIdDesc(); // Recupera tutti gli articoli dal database
        model.addAttribute("articles", articles); // Aggiungi la lista degli articoli al modello
        return "private/editor";
    }



    @PostMapping("/private/saveArticle")
    public String saveOrUpdateArticle(@RequestParam("id") Optional<Long> id,
                                      @RequestParam("content") String content, Model model) {
        try {
            ArticleContent articleContent;
            List<Long> newImageIds = extractImageCodes(content); // Estrai le immagini dal nuovo contenuto

            if (id.isPresent()) {
                // Aggiorna un articolo esistente
                articleContent = articleContentRepository.findById(id.get())
                        .orElseThrow(() -> new IllegalArgumentException("Articolo non trovato"));

                // Trova le immagini attualmente associate
                List<Long> oldImageIds = extractImageCodes(articleContent.getContent());

                // Trova le immagini da eliminare (presenti prima ma non più utilizzate)
                List<Long> imagesToDelete = new ArrayList<>(oldImageIds);
                imagesToDelete.removeAll(newImageIds); // Rimuovi le immagini ancora presenti nel nuovo contenuto

                // Elimina le immagini non più utilizzate
                imagesToDelete.forEach(imageId -> {
                    articleImageRepository.findById(imageId).ifPresent(image -> {
                        articleImageRepository.delete(image);
                        logger.info("Immagine con ID " + imageId + " cancellata.");
                    });
                });
            } else {
                // Crea un nuovo articolo
                articleContent = new ArticleContent();
            }

            // Aggiorna il contenuto dell'articolo
            articleContent.setContent(content);
            articleContentRepository.save(articleContent);

            model.addAttribute("message", "Articolo salvato con successo!");
            return "redirect:/private/editor";

        } catch (Exception e) {
            logger.error("Errore durante il salvataggio dell'articolo", e);
            model.addAttribute("message", "Errore durante il salvataggio dell'articolo");
            return "redirect:/private/editor";
        }
    }







    /**
     * endpoint per restituire i dettagli di un articolo per l'editor.
     */
    @GetMapping("/private/article/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getArticle(@PathVariable Long id) {
        Optional<ArticleContent> article = articleContentRepository.findById(id);

        if (article.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Articolo non trovato"));
        }

        return ResponseEntity.ok(Map.of("id", article.get().getId(), "content", article.get().getContent()));
    }



    /**
     * private/upload-image-article
     */
    @PostMapping("/" + Constants.DOM_LUNA_SAPIENS_PRIVATE_UPLOAD_IMAGE_ARTICLE)
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("upload") MultipartFile file) {
        try {
            logger.info("Sono in upload-image-article");

            // Validazione del tipo MIME
            if (!file.getContentType().startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Formato file non supportato. Carica un'immagine valida."));
            }

            // Validazione della dimensione del file (massimo 2 MB)
            if (file.getSize() > 2_000_000) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "File troppo grande. La dimensione massima è 2MB."));
            }

            // Creazione dell'immagine senza associare un articolo
            ArticleImage image = new ArticleImage();
            image.setFilename(file.getOriginalFilename());
            image.setData(file.getBytes());
            image.setContentType(file.getContentType());
            articleImageRepository.save(image);

            // Creazione dell'URL per recuperare l'immagine
            String imageUrl = "/" + Constants.DOM_LUNA_SAPIENS_IMAGES_ARTICLE + "/" + image.getId();

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
     * images-article
     */
    @Cacheable(value = Constants.IMAGES_ARTICLE, key = "#idImage")
    @GetMapping("/"+Constants.DOM_LUNA_SAPIENS_IMAGES_ARTICLE+"/{idImage}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long idImage) {

        logger.info("sono in "+Constants.DOM_LUNA_SAPIENS_IMAGES_ARTICLE+"/{id}");
        Optional<ArticleImage> image = articleImageRepository.findById(idImage);

        if (image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", image.get().getContentType())
                .body(image.get().getData());
    }



    @DeleteMapping("/private/deleteArticle/{id}")
    public String deleteArticle(@PathVariable Long id, Model model) {
        logger.info("Cancello articolo con ID: " + id);

        // Trova l'articolo
        articleContentRepository.findById(id).ifPresent(article -> {
            // Estrai i codici immagine dal contenuto
            List<Long> imageIds = extractImageCodes(article.getContent());
            logger.info("Codici immagine trovati nell'articolo: " + imageIds);

            // Cancella le immagini associate
            imageIds.forEach(imageId -> {
                articleImageRepository.findById(imageId).ifPresent(image -> {
                    articleImageRepository.delete(image);
                    logger.info("Immagine con ID " + imageId + " cancellata.");
                });
            });

            // Cancella l'articolo
            articleContentRepository.delete(article);
            logger.info("Articolo cancellato con successo.");
        });

        model.addAttribute("message", "Articolo e immagini cancellati con successo!");
        return "redirect:/private/editor";
    }


    private static List<Long> extractImageCodes(String html) {
        // Lista per memorizzare i codici trovati
        List<Long> imageCodes = new ArrayList<>();

        // Regex per trovare i codici nelle sorgenti delle immagini
        String regex = "src=\"/images-article/(\\d+)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);

        // Trova tutti i match e aggiungi i codici alla lista
        while (matcher.find()) {
            String code = matcher.group(1); // Il codice è il primo gruppo catturato
            imageCodes.add(Long.parseLong(code)); // Converte il codice in Long e lo aggiunge
        }

        return imageCodes;
    }






}
