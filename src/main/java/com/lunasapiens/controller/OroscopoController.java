package com.lunasapiens.controller;

import com.lunasapiens.*;
import com.lunasapiens.dto.*;
import com.lunasapiens.entity.ProfiloUtente;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.repository.ProfiloUtenteRepository;
import com.lunasapiens.service.EmailService;
import com.lunasapiens.service.OroscopoGiornalieroService;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;
import com.lunasapiens.zodiac.ServizioTemaNatale;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.*;


@Controller
public class OroscopoController {

    private static final Logger logger = LoggerFactory.getLogger(OroscopoController.class);

    @Autowired
    ServizioOroscopoDelGiorno servizioOroscopoDelGiorno;

    @Autowired
    ServizioTemaNatale servizioTemaNatale;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ProfiloUtenteRepository profiloUtenteRepository;

    private OroscopoGiornalieroService oroscopoGiornalieroService;
    @Autowired
    public OroscopoController(OroscopoGiornalieroService oroscopoGiornalieroService) {
        this.oroscopoGiornalieroService = oroscopoGiornalieroService;
    }


    // #################################### OROSCOPO #####################################

    /**
     * vedi google search console tra le pagine indicizzate c'è questa
     */
    @GetMapping("/oroscopo#!")
    public RedirectView oroscopo_redirect_301() {
        logger.info("sono in oroscopo_redirect_301");
        RedirectView redirectView = new RedirectView("/oroscopo", true);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY); // Imposta il codice 301
        return redirectView;
    }

    /**
     * servizio oroscopo giornaliero
     */
    @GetMapping("/oroscopo")
    public String mostraOroscopo(Model model, @ModelAttribute(Constants.INFO_MESSAGE) String infoMessage,
                                 @ModelAttribute(Constants.INFO_ALERT) String infoAlert,
                                 @ModelAttribute(Constants.INFO_ERROR) String infoError) {

        logger.info("oroscopo endpoint");
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = Utils.GiornoOraPosizione_OggiRomaOre12();
        OroscopoDelGiornoDescrizioneDTO oroscDelGiornDescDTO = servizioOroscopoDelGiorno.oroscopoDelGiornoDescrizioneOggi(giornoOraPosizioneDTO);
        List<OroscopoGiornaliero> listOroscopoGiorn = oroscopoGiornalieroService.findAllByDataOroscopoWithoutVideo(Utils.OggiOre12());
        List<OroscopoGiornalieroDTO> listOroscopoGiornoDTO = new ArrayList<>();
        for(OroscopoGiornaliero oroscopo : listOroscopoGiorn) {
            OroscopoGiornalieroDTO dto = new OroscopoGiornalieroDTO(oroscopo);
            listOroscopoGiornoDTO.add(dto);
        }
        model.addAttribute("oroscDelGiornDescDTO", oroscDelGiornDescDTO);
        model.addAttribute("listOroscopoGiornoDTO", listOroscopoGiornoDTO);

        if(infoMessage != null){
            model.addAttribute(Constants.INFO_MESSAGE, infoMessage);
        }
        if(infoAlert != null){
            model.addAttribute(Constants.INFO_ALERT, infoAlert);
        }
        if(infoError != null){
            model.addAttribute(Constants.INFO_ERROR, infoError);
        }
        return "oroscopo";
    }

    @PostMapping("/"+Constants.DOM_LUNA_SAPIENS_SUBSCRIBE_OROSC_GIORN)
    public String subscribe(@RequestParam("email") @Email @NotEmpty String email, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        logger.info(Constants.DOM_LUNA_SAPIENS_SUBSCRIBE_OROSC_GIORN+" endpoint");
        logger.info("email: "+email);
        Boolean skipEmailSave = (Boolean) request.getAttribute(Constants.SKIP_EMAIL_SAVE);
        if (skipEmailSave != null && skipEmailSave) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Troppe richieste. Sottoscrizione email negata.");
        }else{
            Object[] result = emailService.salvaEmail( email, request.getRemoteAddr() );
            Boolean success = (Boolean) result[0];
            String infoMessage = (String) result[1];
            ProfiloUtente profiloUtente = result[2] instanceof ProfiloUtente ? (ProfiloUtente) result[2] : null;
            if (success && profiloUtente != null) {
                emailService.inviaConfermaEmailOrosciopoGioraliero(profiloUtente);
            }
            redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, infoMessage);
        }
        return "redirect:/oroscopo";
    }

    @GetMapping("/"+Constants.DOM_LUNA_SAPIENS_CONFIRM_EMAIL_OROSC_GIORN)
    public String confirmEmailOroscGiorn(@RequestParam(name = "code", required = true) String code, RedirectAttributes redirectAttributes) {
        logger.info("confirmEmailOroscGiorn endpoint");
        ProfiloUtente profiloUtente = profiloUtenteRepository.findByConfirmationCode( code ).orElse(null);
        String infoMessage = "";
        if(profiloUtente != null && profiloUtente.getConfirmationCode().trim().equals(code.trim())) {
            profiloUtente.setEmailOroscopoGiornaliero(true);
            profiloUtenteRepository.save(profiloUtente);
            redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Grazie per aver confermato la tua email. Sei ora iscritto " +
                    "al nostro servizio di oroscopo giornaliero con l'indirizzo "+profiloUtente.getEmail()+". Presto riceverai il tuo primo " +
                    "oroscopo nella tua casella di posta.");
        }else{
            redirectAttributes.addFlashAttribute(Constants.INFO_ALERT, "Conferma email non riuscita. Iscriviti di nuovo");
        }

        return "redirect:/oroscopo";
    }

    @GetMapping("/"+Constants.DOM_LUNA_SAPIENS_CANCELLA_ISCRIZ_OROSC_GIORN)
    public String cancelEmailOroscGiorn(@RequestParam(name = "code", required = true) String code, RedirectAttributes redirectAttributes) {
        logger.info("cancelEmailOroscGiorn code: "+code);
        ProfiloUtente profiloUtente = profiloUtenteRepository.findByConfirmationCode( code ).orElse(null);
        if(profiloUtente != null && profiloUtente.getConfirmationCode().trim().equals(code.trim())) {
            profiloUtente.setEmailOroscopoGiornaliero(false);
            profiloUtenteRepository.save(profiloUtente);
            redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "La tua cancellazione dall'Oroscopo del giorno è avvenuta con successo. Non riceverai più le nostre previsioni giornaliere. " +
                    "Se desideri iscriverti nuovamente in futuro, visita il nostro sito.");
        }else{
            redirectAttributes.addFlashAttribute(Constants.INFO_ALERT, "L'indirizzo email non è presente nel sistema.");
        }
        return "redirect:/oroscopo";
    }


    /**
     * trasmissione del video
     */
    @Cacheable(value = Constants.VIDEO_CACHE, key = "#videoName")
    @GetMapping("/video-oroscopo-giornaliero/{videoName}")
    public ResponseEntity<ByteArrayResource> streamVideo(@PathVariable String videoName) throws IOException {
        OroscopoGiornaliero oroscopoGiornaliero = oroscopoGiornalieroService.findByNomeFileVideo(videoName)
                .orElseThrow(() -> new NoSuchElementException("Video not found with name: " + videoName));
        if (oroscopoGiornaliero.getVideo() != null) {
            return Utils.VideoResponseEntityByteArrayResource(oroscopoGiornaliero.getVideo());
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
