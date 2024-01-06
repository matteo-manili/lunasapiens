package com.lunasapiens;


import jakarta.servlet.ServletContext;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
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



    private TikTokApiClient tikTokApiClient;
    private ServletContext servletContext;

    @Autowired
    public TikTokController(TikTokApiClient tikTokApiClient, ServletContext servletContext) {
        this.tikTokApiClient = tikTokApiClient;
        this.servletContext = servletContext;
    }

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


    @GetMapping({"/tiktok-outh", "/tiktok-outh/"})
    public String tikTokCallback(@RequestParam String code, @RequestParam String state, Model model) {

        // Verifica lo stato CSRF prima di procedere
        String storedCSRFState = (String) servletContext.getAttribute("csrfStateTikTok");

        if (storedCSRFState == null || !storedCSRFState.equals(state)) {
            // Gestisci l'errore CSRF, ad esempio reindirizzando a una pagina di errore
            model.addAttribute("code", code);
            System.out.println("tiktok-outh tikTokCallback controller ERRATO");
            return "tiktok-outh";
        }

        // Eseguire la richiesta per ottenere l'access token utilizzando il code ottenuto
        String accessToken = exchangeCodeForAccessToken(code);

        // Ora puoi utilizzare l'accessToken per effettuare richieste API a TikTok
        // Implementa qui la logica di gestione dell'accessToken


        model.addAttribute("code", code);
        System.out.println("tiktok-outh tikTokCallback controller OKK");
        return "tiktok-outh";
    }

    private String exchangeCodeForAccessToken(String code) {
        // Implementa la logica per scambiare il code con l'access token utilizzando le API TikTok
        // Usa Apache HttpClient o un'altra libreria HTTP per eseguire la richiesta POST
        // alla URL "https://open.tiktokapis.com/v2/oauth/token/"
        // con i parametri richiesti come descritto nella documentazione
        // Restituisci l'access token ottenuto
        return "il_tuo_access_token";
    }




}
