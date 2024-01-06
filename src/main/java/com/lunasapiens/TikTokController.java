package com.lunasapiens;


import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.InputStream;

@Controller
public class TikTokController {



    @GetMapping("/tiktok/tiktokz8RIHr0Hiiqijh8czuAojvvevrI58VSV.txt")
    public ResponseEntity<byte[]> tiktokVerificationFile() throws IOException {
        // Carica il file TXT specifico
        String fileName = "tiktokz8RIHr0Hiiqijh8czuAojvvevrI58VSV.txt";
        Resource resource = new ClassPathResource("static/" + fileName);

        // Leggi i dati dalla risorsa utilizzando un InputStream
        InputStream inputStream = resource.getInputStream();
        byte[] fileData = IOUtils.toByteArray(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileData.length)
                .contentType(MediaType.TEXT_PLAIN)  // Usa il tipo MIME corretto del tuo file
                .body(fileData);
    }

    //@GetMapping("/tiktok-outh")
    @GetMapping({"/tiktok-outh", "/tiktok-outh/"})
    //public String tikTokRedirect(@RequestParam("code") String authorizationCode, Model model) {
    public String tikTokRedirect(@RequestParam(name="code", required=false, defaultValue="World") String code, Model model) {
        model.addAttribute("code", code);
        System.out.println("tiktok-outh controller");
        return "tiktok-outh";
    }


}