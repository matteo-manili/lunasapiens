package com.lunasapiens.controller;


import com.lunasapiens.entity.Article;
import com.lunasapiens.entity.Image;
import com.lunasapiens.repository.ArticleRepository;
import com.lunasapiens.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
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
public class EditorController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(EditorController.class);

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ImageRepository imageRepository;



    @GetMapping("/private/editor")
    public String editor(Model model) {
        List<Article> articles = articleRepository.findAll(); // Recupera tutti gli articoli dal database
        model.addAttribute("articles", articles); // Aggiungi la lista degli articoli al modello
        return "private/editor";
    }




    @PostMapping("/private/saveArticle")
    public String saveArticle(@RequestParam("content") String content, Model model) {
        logger.info("sono in saveArticle faccio salvataggio");
        Article article = new Article();
        article.setContent(content);

        // Estrai immagini dall'HTML e collegale all'articolo
        List<Image> images = extractImagesFromHtml(content);
        article.setImages(images);

        articleRepository.save(article);

        model.addAttribute("message", "Articolo salvato con successo!");
        return "redirect:/private/editor";
    }



    @PostMapping("/upload-image-article")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("upload") MultipartFile file) {
        try {
            logger.info("sono in upload-image-article");

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

            // Creazione e salvataggio dell'immagine
            Image image = new Image();
            image.setFilename(file.getOriginalFilename());
            image.setData(file.getBytes());
            image.setContentType(file.getContentType());
            imageRepository.save(image);

            // Creazione dell'URL per recuperare l'immagine
            String imageUrl = "/images/" + image.getId();

            // Risposta con l'URL per CKEditor
            Map<String, Object> response = new HashMap<>();
            response.put("url", imageUrl); // Campo richiesto da CKEditor
            response.put("default", imageUrl); // Campo richiesto dal semplice adapter
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Gestione degli errori generici
            logger.error("Errore durante l'upload dell'immagine", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Si è verificato un errore durante il caricamento dell'immagine."));
        }
    }



    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {

        logger.info("sono in images/{id}");
        Optional<Image> image = imageRepository.findById(id);

        if (image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", image.get().getContentType())
                .body(image.get().getData());
    }



    private List<Image> extractImagesFromHtml(String content) {
        // Trova ID delle immagini nell'HTML e recuperale dal database
        List<Image> images = new ArrayList<>();
        Matcher matcher = Pattern.compile("/images/(\\d+)").matcher(content);
        while (matcher.find()) {
            Long id = Long.valueOf(matcher.group(1));
            imageRepository.findById(id).ifPresent(images::add);
        }
        return images;
    }






}
