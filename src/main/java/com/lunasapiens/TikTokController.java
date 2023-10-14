package com.lunasapiens;


import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.file.Files;


@Controller
public class TikTokController {




    @GetMapping("/tiktok/tiktokz8RIHr0Hiiqijh8czuAojvvevrI58VSV.txt")
    public ResponseEntity<byte[]> tiktokVerificationFile() throws IOException {
        // Carica il file TXT specifico
        String fileName = "tiktokz8RIHr0Hiiqijh8czuAojvvevrI58VSV.txt";
        Resource resource = new ClassPathResource("static/"+fileName);
        byte[] fileData = Files.readAllBytes(resource.getFile().toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileData.length)
                .contentType(MediaType.TEXT_PLAIN)  // Usa il tipo MIME corretto del tuo file
                .body(fileData);
    }








        }
