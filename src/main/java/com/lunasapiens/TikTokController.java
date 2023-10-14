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


    @GetMapping({"/videos", "/videos/"})
    public ResponseEntity<byte[]> downloadFile() throws IOException {

        // Carica il file TXT dalla directory delle risorse
        String fileName = "tiktokeVXnQAYg9OQQA35O7IuERbDCTSG5ICWV";
        Resource resource = new ClassPathResource("static/"+fileName+".txt");
        byte[] fileData = Files.readAllBytes(resource.getFile().toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".txt");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileData.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }


}
