package com.lunasapiens;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;


@Controller
public class TikTokController {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RestTemplate restTemplate;




    @GetMapping("/videos/download")
    public ResponseEntity<byte[]> downloadFileTikTok() throws IOException {
        // Carica il file TXT dal percorso specificato, ad esempio dalla directory delle risorse della tua applicazione
        Resource resource = new ClassPathResource("static/file.txt");

        // Controlla se il file esiste
        if (resource.exists()) {
            // Leggi il contenuto del file
            byte[] fileContent = Files.readAllBytes(resource.getFile().toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("tiktokeVXnQAYg9OQQA35O7IuERbDCTSG5ICWV", "tiktokeVXnQAYg9OQQA35O7IuERbDCTSG5ICWV.txt");  // Specifica il nome del file quando viene scaricato

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);
        } else {
            // Se il file non esiste, restituisci una risposta 404
            return ResponseEntity.notFound().build();
        }
    }





}
