package com.lunasapiens.controller;

import com.lunasapiens.*;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.dto.OroscopoGiornalieroDTO;
import com.lunasapiens.entity.EmailUtenti;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.EmailService;
import com.lunasapiens.repository.EmailUtentiRepository;
import com.lunasapiens.service.OroscopoGiornalieroService;
import com.lunasapiens.zodiac.ServiziAstrologici;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;
import java.util.List;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private ScheduledTasks scheduledTasks;

    @Autowired
    ServiziAstrologici servAstrolog;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailUtentiRepository emailUtentiRepository;

    private OroscopoGiornalieroService oroscopoGiornalieroService;


    public final String redirectAttributInfoSubscription = "infoSubscription";


    @Autowired
    public IndexController(OroscopoGiornalieroService oroscopoGiornalieroService) {
        this.oroscopoGiornalieroService = oroscopoGiornalieroService;
    }


    @GetMapping("/")
    public String index(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("message", "Welcome to our dynamic landing page!");
        return "index";
    }




    @GetMapping("/test-invia-email")
    public String inviaEmail(Model model) {


        emailService.sendEmailFromInfoLunaSapiens("matteo.manili@gmail.com", "oggettociaooo", "tesoooooo");

        return "index";
    }



    @GetMapping("/"+Constants.DOM_LUNA_SAPIENS_CONFIRM_EMAIL_OROSC_GIORN)
    public String confirmEmailOroscGiorn(@RequestParam(name = "code", required = true, defaultValue = "") String code, RedirectAttributes redirectAttributes) {
        EmailUtenti emailUtenti = emailUtentiRepository.findByConfirmationCode( code ).orElse(null);
        if(emailUtenti != null && emailUtenti.getConfirmation_code().trim().equals(code.trim())) {
            emailUtenti.setSubscription(true);
            emailUtentiRepository.save(emailUtenti);
            String message = "Grazie per aver confermato la tua email. Sei ora iscritto al nostro servizio di oroscopo giornaliero con l'indirizzo email "+emailUtenti.getEmail()+". " +
                    "Presto riceverai il tuo primo oroscopo nella tua casella di posta.";
            redirectAttributes.addFlashAttribute(redirectAttributInfoSubscription, message);
        }

        return "redirect:/oroscopo";
    }




    @PostMapping("/subscribe")
    public String subscribe(@RequestParam("email") @Email @NotEmpty String email, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        logger.info("email: "+email);
        Boolean skipEmailSave = (Boolean) request.getAttribute(Constants.SKIP_EMAIL_SAVE);
        if (skipEmailSave != null && skipEmailSave) {
            redirectAttributes.addFlashAttribute(redirectAttributInfoSubscription, "Troppe richieste. Sottoscrizione email negata.");

        }else{
            Object[] result = emailService.salvaEmail( email );
            Boolean success = (Boolean) result[0];
            String message = (String) result[1];
            EmailUtenti emailUtenti = result[2] instanceof EmailUtenti ? (EmailUtenti) result[2] : null;
            if (success && emailUtenti != null) {
                emailService.inviaConfermaEmailOrosciopoGioraliero(emailUtenti);
            }

            redirectAttributes.addFlashAttribute(redirectAttributInfoSubscription, message);
        }


        // Esegui un redirect alla pagina oroscopo dopo aver gestito la sottoscrizione
        return "redirect:/oroscopo";

    }




    @GetMapping("/oroscopo")
    public String mostraOroscopo(Model model, @ModelAttribute(redirectAttributInfoSubscription) String infoSubscription) {
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = Util.GiornoOraPosizione_OggiRomaOre12();

        String oroscopoDelGiornoDescrizioneOggi = servAstrolog.oroscopoDelGiornoDescrizioneOggi(giornoOraPosizioneDTO);
        oroscopoDelGiornoDescrizioneOggi = oroscopoDelGiornoDescrizioneOggi.replace("\n", "<br>");

        List<OroscopoGiornaliero> listOroscopoGiorn = oroscopoGiornalieroService.findAllByDataOroscopoWithoutVideo(Util.OggiOre12());
        List<OroscopoGiornalieroDTO> listOroscopoGiornoDTO = new ArrayList<>();
        for(OroscopoGiornaliero oroscopo : listOroscopoGiorn) {
            OroscopoGiornalieroDTO dto = new OroscopoGiornalieroDTO(oroscopo);
            listOroscopoGiornoDTO.add(dto);
        }
        model.addAttribute("oroscopoDelGiornoDescrizioneOggi", oroscopoDelGiornoDescrizioneOggi);
        model.addAttribute("videos", listOroscopoGiornoDTO);

        // Aggiungi infoSubscription al modello per essere visualizzato nella vista
        model.addAttribute(redirectAttributInfoSubscription, infoSubscription);

        return "oroscopo";
    }



    @Cacheable(value = Constants.VIDEO_CACHE, key = "#videoName")
    @GetMapping("/oroscopo-giornaliero/{videoName}")
    public ResponseEntity<ByteArrayResource> streamVideo(@PathVariable String videoName) throws IOException {
        OroscopoGiornaliero oroscopoGiornaliero = oroscopoGiornalieroService.findByNomeFileVideo(videoName)
                .orElseThrow(() -> new NoSuchElementException("Video not found with name: " + videoName));
        if (oroscopoGiornaliero.getVideo() != null) {
            return Util.VideoResponseEntityByteArrayResource(oroscopoGiornaliero.getVideo());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * lo uso solo per test
     * @param model
     * @return
     */
    @GetMapping("/genera-video")
    public String gerneraVideo(Model model) {
        //scheduledTasks.test_Oroscopo_Segni_Transiti_Aspetti();
        scheduledTasks.creaOroscopoGiornaliero();
        return "index";
    }


    @GetMapping("/info-privacy")
    public String infoPrivacy(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "info-privacy";
    }

    @GetMapping("/termini-di-servizio")
    public String terminiDiServizio(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "termini-di-servizio";
    }


    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        model.addAttribute("infoError", "Errore generale.");
        return "error";
    }


}
