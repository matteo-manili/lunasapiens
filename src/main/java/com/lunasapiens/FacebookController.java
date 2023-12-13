package com.lunasapiens;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunasapiens.model.FacebookConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.net.URI;


@Controller
public class FacebookController {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/facebook")
    public String facebook(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {

        FacebookConfig fConfig  = appConfig.getfacebookConfig();

        try{


            /*
            "https://graph.facebook.com/oauth/access_token?
            client_id=XXX
            &redirect_uri=XXX
            &client_secret=XXX
            &scopes=public_profile,pages_manage_cta,manage_pages,publish_pages
            &code=code"
             */


            String url = "https://graph.facebook.com/" +fConfig.getVersion()+ "/oauth/access_token" +
                    "?client_id=" + fConfig.getAppId() +
                    //"&redirect_uri=http://localhost:8081/facebook" +
                    "&client_secret=" + fConfig.getAppSecret() +
                    //"&scopes=" + "public_profile,pages_manage_cta,manage_pages,publish_pages,pages_read_engagement,pages_manage_posts"+
                    "&scopes=" + "pages_read_engagement,pages_manage_posts"+
                    "&grant_type=client_credentials";

                    //"&code=" + "code";

            System.out.println("client_id: "+fConfig.getAppId() +" client_secret: "+fConfig.getAppSecret() +" version: "+fConfig.getVersion());
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            System.out.println( response.getBody() );


            // Parse la risposta JSON usando Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            String accessTokenTEMP = jsonNode.get("access_token").asText();


/*
            4 If posting to a group, requires app being installed in the group, and
                    either publish_to_groups permission with user token, or both pages_read_engagement
                     and pages_manage_posts permission with page token; If posting to a page,
                   requires both pages_read_engagement and pages_manage_posts as an admin with
                    sufficient administrative permission","type":"OAuthException","code":200,"fbtrace_id":"AqrC0pr_sO01zQo6Y-FaAQJ
*/



            // -----------------------------------

            /*
            Come trovare l'ID della Pagina Facebook
            Ecco come trovare l'ID della Pagina Facebook dal computer:
            Apri la Pagina di cui vuoi trovare l'ID.
            Dal menu sotto il nome della Pagina, clicca su Altro.
            Clicca su Informazioni. In Maggiori informazioni, vai a ID della Pagina.
            VEDERE ANCHE IMMAGINE SCREENSHOT NELLA CARTELLA STATIC
             */

            String ID_PAGE = "130228343511175";



            String message = "Hello World!";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url_2 = "https://graph.facebook.com/" + ID_PAGE + "/feed";
            // Costruisci il corpo della richiesta
            String requestBody = "message=" + message + "&access_token=" + accessTokenTEMP + "&permission=pages_read_engagement,pages_manage_posts";
            // Costruisci l'oggetto RequestEntity con il corpo e le intestazioni
            RequestEntity<String> requestEntity = new RequestEntity<>(requestBody, headers, HttpMethod.POST, new URI(url_2));
            // Esegui la richiesta
            ResponseEntity<String> response_2 = restTemplate.exchange(requestEntity, String.class);

            System.out.println( response_2.getBody() );


            model.addAttribute("result", response.getBody()  );
        }catch(Exception exc){
            exc.printStackTrace();

        }

        model.addAttribute("result", "messaggio statico facebooook"  );
        return "facebook";
    }





}
