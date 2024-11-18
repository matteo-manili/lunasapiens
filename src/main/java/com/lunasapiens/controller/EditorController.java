package com.lunasapiens.controller;


import com.lunasapiens.entity.Article;
import com.lunasapiens.entity.Image;
import com.lunasapiens.repository.ArticleRepository;
import com.lunasapiens.repository.ImageRepository;
import com.lunasapiens.repository.ProfiloUtenteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public String editor() {
        return "private/editor";
    }



    @PostMapping("/private/saveArticle")
    public String saveArticle(@RequestParam("content") String content, Model model) {
        Article article = new Article();
        article.setContent(content);

        // Estrai immagini dall'HTML e collegale all'articolo
        List<Image> images = extractImagesFromHtml(content);
        article.setImages(images);

        articleRepository.save(article);

        model.addAttribute("message", "Articolo salvato con successo!");
        return "redirect:/private/editor";
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



    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("upload") MultipartFile file) {
        try {
            logger.info("sono in uploadImage");
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
            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Gestione degli errori generici
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Si è verificato un errore durante il caricamento dell'immagine."));
        }

    }


    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Image image = imageRepository.findById(id).orElseThrow(() -> new RuntimeException("Image not found"));
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(image.getContentType()))
                .body(image.getData());
    }


}
