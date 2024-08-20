package com.lunasapiens.controller;

import com.lunasapiens.Constants;
import com.lunasapiens.ScheduledTasks;
import com.lunasapiens.config.FacebookConfig;
import com.lunasapiens.service.EmailService;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;
import com.lunasapiens.zodiac.ServizioTemaNatale;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.ByteArrayInputStream;


@Controller
public class DocumentiController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentiController.class);

    @Autowired
    private ScheduledTasks scheduledTasks;

    @Autowired
    ServizioOroscopoDelGiorno servizioOroscopoDelGiorno;

    @Autowired
    ServizioTemaNatale servizioTemaNatale;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FacebookConfig facebookConfig;



    @GetMapping("/documenti")
    public String register(Model model, HttpServletRequest request) {
        return "documenti";
    }



    @GetMapping("/view-pdf")
    public ResponseEntity<Resource> viewPDF(@RequestParam String fileName) {

        logger.info("sono in DocumentiController viewPDF");

        // URL base del repository GitHub dove si trovano i PDF
        String baseUrl = "https://github.com/matteo-manili/lunasapiens_download/raw/main/";

        // Costruzione del link completo per il download
        String pdfUrl = baseUrl + fileName;

        try {
            // Usa RestTemplate per scaricare il PDF dal repository GitHub
            RestTemplate restTemplate = new RestTemplate();
            byte[] pdfBytes = restTemplate.getForObject(pdfUrl, byte[].class);

            // Crea una risorsa per la risposta
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdfBytes));

            // Imposta il tipo di contenuto e il nome del file
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfBytes.length)
                    .body(resource);

        } catch (Exception e) {
            // Gestisci errori, ad esempio file non trovato
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }



    // Metodo per il download del PDF
    @GetMapping("/download-pdf")
    public RedirectView downloadPDF(@RequestParam String fileName, RedirectAttributes attributes) {
        // URL base del repository GitHub dove si trovano i PDF
        String baseUrl = "https://github.com/matteo-manili/lunasapiens_download/raw/main/";
        // esempio: https://github.com/matteo-manili/lunasapiens_download/raw/main/test_1.pdf

        // Costruzione del link completo per il download
        String pdfUrl = baseUrl + fileName;

        logger.info("Redirecting to PDF: " + pdfUrl);

        // Redirige l'utente al file PDF su GitHub
        return new RedirectView(pdfUrl);
    }


    /**
     *
     * LIBRI E FILE PATH IN ELENCO NELLA PAGINA
     *
     Bondanini N. - Progressioni secondarie
     Bondanini_Progressioni_secondarie.pdf

     I Dispositori secondo Ciro Discepolo 1998
     I_Dispositori_secondo_Ciro_Discepolo_1998.pdf

     Glossario dei termini astrologici & Tabelle sintetiche RAE ORION
     Glossario_dei_termini_astrologici_e_Tabelle_sintetiche_RAE_ORION.pdf

     La Domificazione Segni Case Ellenistica
     LA_DOMIFICAZIONE_SEGNI_CASE_Ellenistica.pdf

     Pianeti nelle case
     Pianeti-nelle-case.pdf

     Case astrologiche
     Case_astrologiche.pdf

     Le case astrologiche, nuovi significati
     Capone_Federico_Le_case_astrologiche_nuovi_significati.pdf

     L'Eclair André - Astrologia eretica
     L_Eclair_Andre_Astrologia_eretica.pdf

     Guida all' astrologia - parte seconda
     Linee_guida_all_astrologia_seconda_parte.odt

     IL TEMA KARMICO
     IL_TEMA_KARMICO.docx

     Astrologia Karmico-Evolutiva e Vibrazionale
     Astrologia_Karmico_Evolutiva_e_Vibrazionale.pdf

     Omeopatia e Astrologia
     Omeopatia_e_Astrologia.pdf

     Tibetan Astrology
     Tibetan_Astrology.pdf

     Chinese Astrology - Exploring The Eastern Zodiac
     Chinese_Astrology_Exploring_The_Eastern_Zodiac.pdf

     Astrologia messicana, una sintesi astrologica
     Guajardo_Gloria_Astrologia_messicana_una_sintesi_astrologica.pdf

     Astrologia medica
     Astrologia_medica.pdf

     La Luna in astrologia
     La_Luna_in_astrologia.pdf

     Le esaltazioni
     biosferanoosfera.pdf

     Il Tema Progresso
     tema_progresso.pdf

     CIA Centro Italiano di Astrologia Linguaggio astrale dal 1970 2004
     CIA_Centro_Italiano_di_Astrologia_Linguaaggio_astrale_dal_1970_2004.pdf

     Astrology of the Seers
     Frawley_David_Astrology_of_the_Seers.pdf

     */





}

