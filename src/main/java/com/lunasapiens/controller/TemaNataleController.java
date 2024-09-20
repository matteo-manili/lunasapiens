package com.lunasapiens.controller;

import com.lunasapiens.*;
import com.lunasapiens.dto.*;
import com.lunasapiens.entity.ProfiloUtente;
import com.lunasapiens.repository.ProfiloUtenteRepository;
import com.lunasapiens.service.EmailService;
import com.lunasapiens.zodiac.BuildInfoAstrologiaAstroSeek;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;
import com.lunasapiens.zodiac.ServizioTemaNatale;
import com.theokanning.openai.completion.chat.ChatMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.*;


@Controller
public class TemaNataleController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(TemaNataleController.class);

    @Autowired
    ServizioOroscopoDelGiorno servizioOroscopoDelGiorno;

    @Autowired
    ServizioTemaNatale servizioTemaNatale;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ProfiloUtenteRepository profiloUtenteRepository;

    // #################################### TEMA NATALE #####################################

    /**
     * servizio tema natale
     */
    @GetMapping("/tema-natale")
    public String temaNatale(Model model, @ModelAttribute("dateTime") String datetime,
                             @ModelAttribute("cityInput") String cityInput,
                             @ModelAttribute("cityName") String cityName,
                             @ModelAttribute("regioneName") String regioneName,
                             @ModelAttribute("statoName") String statoName,
                             @ModelAttribute("statoCode") String statoCode,
                             @ModelAttribute("cityLat") String cityLat,
                             @ModelAttribute("cityLng") String cityLng,
                             @ModelAttribute("temaNataleDescrizione") String temaNataleDescrizione,
                             @ModelAttribute("paginaChatId") String paginaChatId,
                             @ModelAttribute(Constants.USER_SESSION_ID) String userSessionId,
                             @AuthenticationPrincipal UserDetails userDetails
    ) {

        logger.info("sono in temaNatale");
        if( userDetails != null ){ logger.info("userDetails: "+userDetails.getUsername()); }

        LocalDateTime defaultDateTime = LocalDateTime.of(1980, 1, 1, 0, 0);
        Optional<String> optionalDateTime = Optional.ofNullable(datetime);
        optionalDateTime
                .filter(dateTimeString -> !dateTimeString.isEmpty())
                .ifPresentOrElse(
                        presentDateTime -> model.addAttribute("dateTime", presentDateTime),
                        () -> model.addAttribute("dateTime", defaultDateTime.format(Constants.DATE_TIME_LOCAL_FORMATTER)));

        // Only add attributes if they are not null or empty
        Optional.ofNullable(cityInput).filter(input -> !input.isEmpty()).ifPresent(input -> model.addAttribute("cityInput", input));
        Optional.ofNullable(cityName).filter(name -> !name.isEmpty()).ifPresent(name -> model.addAttribute("cityName", name));
        Optional.ofNullable(regioneName).filter(region -> !region.isEmpty()).ifPresent(region -> model.addAttribute("regioneName", region));
        Optional.ofNullable(statoName).filter(state_name -> !state_name.isEmpty()).ifPresent(state_name -> model.addAttribute("statoName", state_name));
        Optional.ofNullable(statoCode).filter(state_code -> !state_code.isEmpty()).ifPresent(state_code -> model.addAttribute("statoCode", state_code));
        Optional.ofNullable(cityLat).filter(lat -> !lat.isEmpty()).ifPresent(lat -> model.addAttribute("cityLat", lat));
        Optional.ofNullable(cityLng).filter(lng -> !lng.isEmpty()).ifPresent(lng -> model.addAttribute("cityLng", lng));
        Optional.ofNullable(temaNataleDescrizione).filter(description -> !description.isEmpty()).ifPresent(description -> model.addAttribute("temaNataleDescrizione", description));
        Optional.ofNullable(paginaChatId).filter(id -> !id.isEmpty()).ifPresent(id -> model.addAttribute("paginaChatId", id));
        Optional.ofNullable(userSessionId).filter(id -> !id.isEmpty()).ifPresent(id -> model.addAttribute(Constants.USER_SESSION_ID, id));


        return "tema-natale";
    }

    /**
     * questa pagina è indicizzata da google ma gli da errori, quindi va gestita e fatto redirect alla pagina canonica
     * @return
     */
    @GetMapping("/tema")
    public RedirectView tema() {
        RedirectView redirectView = new RedirectView("/tema-natale", true);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY); // Imposta il codice 301
        return redirectView;
    }

    @GetMapping("/temaNataleSubmit")
    public String temaNataleSubmit(@RequestParam("dateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime,
                                   @RequestParam("cityLat") String cityLat,
                                   @RequestParam("cityLng") String cityLng,
                                   @RequestParam("cityName") String cityName,
                                   @RequestParam("regioneName") String regioneName,
                                   @RequestParam("statoName") String statoName,
                                   @RequestParam("statoCode") String statoCode,
                                   RedirectAttributes redirectAttributes, HttpServletRequest request) {

        logger.info("sono in temaNataleSubmit");
        HttpSession session = request.getSession(true); // Crea una nuova sessione se non esiste
        String userId = (String) session.getAttribute(Constants.USER_SESSION_ID);
        if (userId == null) {
            userId = UUID.randomUUID().toString(); // Genera un nuovo ID solo se non esiste
            session.setAttribute(Constants.USER_SESSION_ID, userId);
        }

        // Estrai le singole componenti della data e ora
        int hour = datetime.getHour();
        int minute = datetime.getMinute();
        int day = datetime.getDayOfMonth();
        int month = datetime.getMonthValue();
        int year = datetime.getYear();

        String luogoNascita = String.join(", ", cityName, regioneName, statoName);

        logger.info("Ora: " + hour); logger.info("Minuti: " + minute); logger.info("Giorno: " + day); logger.info("Mese: " + month); logger.info("Anno: " + year);
        logger.info("cityName: " + cityName); logger.info("regioneName: " + regioneName); logger.info("statoName: " + statoName); logger.info("statoCode: " + statoCode);
        logger.info("Latitude: " + cityLat); logger.info("Longitude: " + cityLng); logger.info(Constants.USER_SESSION_ID + userId);

        // Prepara i dati da passare tramite redirectAttributes
        redirectAttributes.addFlashAttribute("cityInput", cityName + ", " + regioneName + ", " + statoName);
        redirectAttributes.addFlashAttribute("cityName", cityName);
        redirectAttributes.addFlashAttribute("regioneName", regioneName);
        redirectAttributes.addFlashAttribute("statoName", statoName);
        redirectAttributes.addFlashAttribute("statoCode", statoCode);
        redirectAttributes.addFlashAttribute("cityLat", cityLat);
        redirectAttributes.addFlashAttribute("cityLng", cityLng);
        redirectAttributes.addFlashAttribute("dateTime", datetime.format(Constants.DATE_TIME_LOCAL_FORMATTER));
        redirectAttributes.addFlashAttribute("dataOraNascita", datetime.format(Constants.DATE_TIME_FORMATTER));
        redirectAttributes.addFlashAttribute("luogoNascita", cityName + ", " + regioneName + ", " + statoName);
        redirectAttributes.addFlashAttribute(Constants.USER_SESSION_ID, userId);

        final GiornoOraPosizioneDTO giornoOraPosizioneDTO = new GiornoOraPosizioneDTO(hour, minute, day, month, year, Double.parseDouble(cityLat), Double.parseDouble(cityLng));
        CoordinateDTO coordinateDTO = new CoordinateDTO(cityName, regioneName, statoName, statoCode);
        StringBuilder temaNataleDescrizione = servizioTemaNatale.temaNataleDescrizione_AstrologiaAstroSeek(giornoOraPosizioneDTO, coordinateDTO);
        StringBuilder significatiTemaNataleDescrizione = servizioTemaNatale.significatiTemaNataleDescrizione();
        temaNataleDescrizione.append( significatiTemaNataleDescrizione );
        redirectAttributes.addFlashAttribute("temaNataleDescrizione", temaNataleDescrizione.toString());

        String paginaChatId = UUID.randomUUID().toString();
        redirectAttributes.addFlashAttribute("paginaChatId", paginaChatId);
        logger.info("paginaChatId: " + paginaChatId);

        // Metto in cache i chatMessageIa
        Cache cache = cacheManager.getCache(Constants.MESSAGE_BOT_CACHE);
        if (cache == null) {
            logger.error("Cache not found: " + Constants.MESSAGE_BOT_CACHE);
            return "redirect:/tema-natale";
        }
        List<ChatMessage> chatMessageIa = new ArrayList<>();
        StringBuilder temaNataleDescIstruzioniBOTSystem = BuildInfoAstrologiaAstroSeek.temaNataleIstruzioneBOTSystem(temaNataleDescrizione.toString(), datetime, luogoNascita);
        logger.info( "temaNataleDescrizioneIstruzioneBOTSystem: "+temaNataleDescIstruzioniBOTSystem );
        chatMessageIa.add(new ChatMessage("system", temaNataleDescIstruzioniBOTSystem.toString() ));
        cache.put(paginaChatId, chatMessageIa);
        return "redirect:/tema-natale";
    }





    @PostMapping("/"+Constants.DOM_LUNA_SAPIENS_SUBSCRIBE_TEMA_NATALE)
    public String subscribe_tn(@RequestParam("email") @Email @NotEmpty String email, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        logger.info(Constants.DOM_LUNA_SAPIENS_SUBSCRIBE_TEMA_NATALE+" endpoint");
        logger.info("email: "+email);
        Boolean skipEmailSave = (Boolean) request.getAttribute(Constants.SKIP_EMAIL_SAVE);
        if (skipEmailSave != null && skipEmailSave) {
            redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Troppe richieste. Sottoscrizione email negata.");
        }else{
            Object[] result = emailService.salvaEmail( email, request.getRemoteAddr() );
            Boolean success = (Boolean) result[0];
            String infoMessage = (String) result[1];
            ProfiloUtente profiloUtente = result[2] instanceof ProfiloUtente ? (ProfiloUtente) result[2] : null;
            if (success && profiloUtente != null) {
                emailService.inviaConfermaEmailTemaNatale(profiloUtente);
            }
            redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, infoMessage);
        }
        return "redirect:/tema-natale";
    }


    @GetMapping("/"+Constants.DOM_LUNA_SAPIENS_CONFIRM_EMAIL_TEMA_NATALE)
    public String confirmEmailTemaNatale(@RequestParam(name = "code", required = true) String code, RedirectAttributes redirectAttributes) {
        logger.info(Constants.DOM_LUNA_SAPIENS_CONFIRM_EMAIL_TEMA_NATALE);
        ProfiloUtente profiloUtente = profiloUtenteRepository.findByConfirmationCode( code ).orElse(null);
        String infoMessage = "";
        if(profiloUtente != null && profiloUtente.getConfirmationCode().trim().equals(code.trim())) {
            profiloUtente.setEmailAggiornamentiTemaNatale( false ); // TODO lo metto false altrimenti arriva all'utente l'email del Oroscopo del giorno
            profiloUtenteRepository.save(profiloUtente);
            infoMessage = "Grazie per aver confermato la tua email. Sei ora iscritto per ricevere gli aggiornamenti del Tema Natale IA con l'indirizzo " +
                    ""+profiloUtente.getEmail();
        }else{
            infoMessage = "Conferma email non riuscita. Iscriviti di nuovo";
        }
        redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, infoMessage);
        return "redirect:/tema-natale";
    }

}
