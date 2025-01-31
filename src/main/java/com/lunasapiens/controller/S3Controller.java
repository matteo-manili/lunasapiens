package com.lunasapiens.controller;

import com.lunasapiens.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;



@Controller
public class S3Controller extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(S3Controller.class);

    @Autowired
    private S3Service s3Service;



    /**
     * Endpoint per il caricamento di un file su S3
     */
    @PostMapping("/s3-upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Carica il file nel bucket S3
            String fileName = file.getOriginalFilename();
            if (fileName == null || fileName.isEmpty()) {
                return ResponseEntity.badRequest().body("Nome del file non valido.");
            }
            s3Service.uploadFile(fileName, file.getInputStream());

            return ResponseEntity.ok("File caricato con successo: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel caricamento del file: " + e.getMessage());
        }
    }

    /**
     * Endpoint per ottenere l'URL presigned per il download di un file
     * sarebbe la creazione di un url che ha una validità temporanea definita e che è usato per scaricare un'immagine (o un file)
     * del bucket dall'esterno senza autenticazione.
     */
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

    /**
     * Endpoint per il download di un file direttamente
     */
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

    /**
     * Endpoint per eliminare un file dal bucket S3
     */
    @DeleteMapping("/s3-delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        try {
            // Elimina il file dal bucket S3
            s3Service.deleteFile(fileName);

            return ResponseEntity.ok("File eliminato con successo: " + fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'eliminazione del file: " + e.getMessage());
        }
    }
}
