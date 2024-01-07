package com.lunasapiens;

import com.lunasapiens.entity.GestioneApplicazione;
import com.lunasapiens.repository.GestioneApplicazioneRepository;
import com.lunasapiens.service.OperazioniDbTikTokService;
import jakarta.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.UriComponentsBuilder;


@Controller
public class ZZZ_TEST_Controller {

    private static final Logger logger = LoggerFactory.getLogger(TikTokApiClient.class);


    @GetMapping("/test")
    public String ZZZ_TEST() {
        try{

            //doAutenticazioneTikTok();

        }catch (Exception e){
            e.printStackTrace();
        }
        return "ZZZ_TEST";
    }





}
