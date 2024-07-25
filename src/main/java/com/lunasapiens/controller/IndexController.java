package com.lunasapiens.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunasapiens.Constants;
import com.lunasapiens.ScheduledTasks;
import com.lunasapiens.Util;
import com.lunasapiens.dto.ContactFormDTO;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.dto.OroscopoDelGiornoDescrizioneDTO;
import com.lunasapiens.dto.OroscopoGiornalieroDTO;
import com.lunasapiens.entity.EmailUtenti;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.EmailService;
import com.lunasapiens.repository.EmailUtentiRepository;
import com.lunasapiens.service.OroscopoGiornalieroService;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;
import com.lunasapiens.zodiac.ServizioTemaNatale;
import com.theokanning.openai.completion.chat.ChatMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private String getApiGeonamesUsername;

    @Autowired
    private ScheduledTasks scheduledTasks;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ServizioOroscopoDelGiorno servizioOroscopoDelGiorno;

    @Autowired
    ServizioTemaNatale servizioTemaNatale;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailUtentiRepository emailUtentiRepository;

    @Autowired
    private CacheManager cacheManager;

    private OroscopoGiornalieroService oroscopoGiornalieroService;
    @Autowired
    public IndexController(OroscopoGiornalieroService oroscopoGiornalieroService) {
        this.oroscopoGiornalieroService = oroscopoGiornalieroService;
    }

    private static final String INFO_MESSAGE = "infoMessage";
    private static final String INFO_ERROR = "infoError";


    @GetMapping("/")
    public String index(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute(INFO_MESSAGE, "Welcome to our dynamic landing page!");
        return "index";
    }


    /**
     * servizio tema natale
     */
    @GetMapping("/tema-natale")
    public String temaNatale(Model model, @ModelAttribute("dateTime") String datetime) {

        logger.info("AAA datetime="+datetime);
        LocalDateTime defaultDateTime = LocalDateTime.of(1980, 1, 1, 0, 0);
        Optional<String> optionalDateTime = Optional.ofNullable(datetime);
        optionalDateTime
                .filter(dateTimeString -> !dateTimeString.isEmpty())
                .ifPresentOrElse(
                        presentDateTime -> model.addAttribute("dateTime", presentDateTime),
                        () -> model.addAttribute("dateTime", defaultDateTime.format(Constants.DATE_TIME_LOCAL_FORMATTER))
                );


        // ############ Dati per test, togliere poi!!! ############
        model.addAttribute("temaNataleDescrizione", "ciao bello ciao bello ciao bello ciao bello ciao bello <br> ciao bello ciao bello <br>ciao bello ciao bello <br>ciao bello ciao bello <br>ciao bello ciao bello <br>ciao bello ciao bello <br>");
        model.addAttribute("cityInput","Roma, Lazio, Italia");
        model.addAttribute("cityName", "Roma");
        model.addAttribute("regioneName", "Lazio");
        model.addAttribute("statoName", "Italia");
        model.addAttribute("cityLat", "41.89193");
        model.addAttribute("cityLng", "12.51133");
        model.addAttribute("luogoNascita", "Roma, Lazio, Italia");


        return "tema-natale";
    }

    @GetMapping("/temaNataleSubmit")
    public String temaNataleSubmit(@RequestParam("dateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime, @RequestParam("cityLat") String cityLat,
                                   @RequestParam("cityLng") String cityLng, @RequestParam("cityName") String cityName, @RequestParam("regioneName") String regioneName,
                                   @RequestParam("statoName") String statoName, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        // Estrai le singole componenti della data e ora
        int hour = datetime.getHour();
        int minute = datetime.getMinute();
        int day = datetime.getDayOfMonth();
        int month = datetime.getMonthValue();
        int year = datetime.getYear();

        logger.info("Ora: " + hour);
        logger.info("Minuti: " + minute);
        logger.info("Giorno: " + day);
        logger.info("Mese: " + month);
        logger.info("Anno: " + year);
        logger.info("cityName: " + cityName);
        logger.info("regioneName: " + regioneName);
        logger.info("statoName: " + statoName);
        logger.info("Latitude: " + cityLat);
        logger.info("Longitude: " + cityLng);

        redirectAttributes.addFlashAttribute("cityInput", cityName+", "+regioneName+", "+statoName);
        redirectAttributes.addFlashAttribute("cityName", cityName);
        redirectAttributes.addFlashAttribute("regioneName", regioneName);
        redirectAttributes.addFlashAttribute("statoName", statoName);
        redirectAttributes.addFlashAttribute("cityLat", cityLat);
        redirectAttributes.addFlashAttribute("cityLng", cityLng);
        redirectAttributes.addFlashAttribute("dateTime", datetime.format( Constants.DATE_TIME_LOCAL_FORMATTER ));
        redirectAttributes.addFlashAttribute("dataOraNascita", datetime.format( Constants.DATE_TIME_FORMATTER ));
        redirectAttributes.addFlashAttribute("luogoNascita", cityName+", "+regioneName+", "+statoName);
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = new GiornoOraPosizioneDTO(hour, minute, day, month, year, Double.parseDouble(cityLat), Double.parseDouble(cityLng));

        String temaNataleDescrizione = servizioTemaNatale.temaNataleDescrizione(giornoOraPosizioneDTO);
        redirectAttributes.addFlashAttribute("temaNataleDescrizione", temaNataleDescrizione);



        // TODO creare il cookie e metterci dentro un codice random che indentifica l'utente.
        // rendere il cookie sicuro con gli attributi: SomeSite=Strict (evita il cross, cioè tra più siti), HttpOnly (non acceessbibile da javascripy), Securo (cioè solo via https)
        // Creare un identificativo random per l'utente
        String userId = UUID.randomUUID().toString();
        // Creare il cookie e settare gli attributi
        Cookie userCookie = new Cookie("userId", userId);
        userCookie.setHttpOnly(true); // Il cookie non è accessibile tramite JavaScript
        userCookie.setPath("/"); // Il cookie è disponibile per tutto il sito
        // Aggiungere il cookie alla risposta
        response.addCookie(userCookie);
        // Aggiungere manualmente l'attributo SameSite
        response.setHeader("Set-Cookie", userCookie.getName() + "=" + userCookie.getValue() + "; HttpOnly; SameSite=Strict");



        // TODO creare una nuova sezione nella cash cafferine.
        // che è costituira da una lista di elementio che rappresenmta la conversazione col bot gpt.
        Cache cache = cacheManager.getCache(Constants.TEMA_NATALE_BOT_CACHE);
        List<ChatMessage> chatMessageIa = new ArrayList<>();
        String temaNataleDescrizioneIstruzioneBOTSystem = "Rispondi alle domande dell'utente in base al suo tema natale: " + temaNataleDescrizione;
        chatMessageIa.add(new ChatMessage("system", temaNataleDescrizioneIstruzioneBOTSystem));
        cache.put(userId, chatMessageIa);


        // TODO creare processo -+
        /*
        quando l'utente invia il messaggio per fare una domanda al bot, tramite websocket:

        recuper il userId dal cookie dello user
        trtamite lo userId recupero la lista List<ChatMessage> chatMessageIa dell'utente

        aggiungo un nuovo messagge alla lista in cache: chatMessageIa.add(new ChatMessage("user", domandaUser));
        cache.put(userId, chatMessageIa);

        faccio partuire il metodo della open ai e passo il la lista.

        quando la IA risponde aggiungo la risposta alla lista
        chatMessageIa.add(new ChatMessage("assistant", risposta));
        e riaggiunro la lista in cache.
        cache.put(userId, chatMessageIa);

        e così via per ogni domanda.

        all'inizio si c'è un unico messaggio al System, poi le domande che vanno in "user" e le risposte della IA vanno in "assistant"


         */



        return "redirect:/tema-natale";
    }

    /**
     * Web Socket........
     * @param message
     * @return
     */
    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public String handleMessage(String message) {
        // Logica di elaborazione del messaggio
        //return "{\"content\": \"Risposta dal server: " + message + "\"}";

        logger.info("web sockeeeettttt");

        return "ciao belllo dal serverrr";

    }



    @GetMapping("/coordinate")
    @ResponseBody
    public List<Map<String, Object>> getCoordinates(@RequestParam String cityName) {
        String url = "http://api.geonames.org/searchJSON?name_startsWith=" + cityName + "&username=" + getApiGeonamesUsername + "&style=MEDIUM&lang=it&maxRows=5";

        logger.info(url);
        String response = restTemplate.getForObject(url, String.class);
        List<Map<String, Object>> locations = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode geonames = root.path("geonames");
            for (JsonNode node : geonames) {
                // Controlla se il fcl è uguale a "P"
                if ("P".equals(node.path("fcl").asText())) {
                    String name = node.path("name").asText();
                    String adminName1 = node.path("adminName1").asText();
                    String countryCode = node.path("countryCode").asText();
                    String uniqueKey = name + "|" + adminName1 + "|" + countryCode;
                    // Aggiungi solo se non è già stato visto
                    if (!seen.contains(uniqueKey)) {
                        seen.add(uniqueKey);
                        Map<String, Object> location = new HashMap<>();
                        location.put("name", name);
                        location.put("adminName1", adminName1);
                        location.put("countryName", node.path("countryName").asText());
                        location.put("countryCode", countryCode);
                        location.put("lat", node.path("lat").asText());
                        location.put("lng", node.path("lng").asText());
                        locations.add(location);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return locations;
    }


    /**
     * servizio oroscopo giornaliero
     */
    @GetMapping("/oroscopo")
    public String mostraOroscopo(Model model, @ModelAttribute(INFO_MESSAGE) String infoMessage) {

        logger.info("oroscopo endpoint");
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = Util.GiornoOraPosizione_OggiRomaOre12();
        OroscopoDelGiornoDescrizioneDTO oroscDelGiornDescDTO = servizioOroscopoDelGiorno.oroscopoDelGiornoDescrizioneOggi(giornoOraPosizioneDTO);
        List<OroscopoGiornaliero> listOroscopoGiorn = oroscopoGiornalieroService.findAllByDataOroscopoWithoutVideo(Util.OggiOre12());
        List<OroscopoGiornalieroDTO> listOroscopoGiornoDTO = new ArrayList<>();
        for(OroscopoGiornaliero oroscopo : listOroscopoGiorn) {
            OroscopoGiornalieroDTO dto = new OroscopoGiornalieroDTO(oroscopo);
            listOroscopoGiornoDTO.add(dto);
        }
        model.addAttribute("oroscDelGiornDescDTO", oroscDelGiornDescDTO);
        model.addAttribute("listOroscopoGiornoDTO", listOroscopoGiornoDTO);

        // Aggiungi infoMessage al modello per essere visualizzato nella vista
        model.addAttribute(INFO_MESSAGE, infoMessage);
        return "oroscopo";
    }

    @PostMapping("/"+Constants.DOM_LUNA_SAPIENS_SUBSCRIBE_OROSC_GIORN)
    public String subscribe(@RequestParam("email") @Email @NotEmpty String email, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        logger.info(Constants.DOM_LUNA_SAPIENS_SUBSCRIBE_OROSC_GIORN+" endpoint");
        logger.info("email: "+email);
        Boolean skipEmailSave = (Boolean) request.getAttribute(Constants.SKIP_EMAIL_SAVE);
        if (skipEmailSave != null && skipEmailSave) {
            redirectAttributes.addFlashAttribute(INFO_MESSAGE, "Troppe richieste. Sottoscrizione email negata.");
        }else{
            Object[] result = emailService.salvaEmail( email );
            Boolean success = (Boolean) result[0];
            String infoMessage = (String) result[1];
            EmailUtenti emailUtenti = result[2] instanceof EmailUtenti ? (EmailUtenti) result[2] : null;
            if (success && emailUtenti != null) {
                emailService.inviaConfermaEmailOrosciopoGioraliero(emailUtenti);
            }
            redirectAttributes.addFlashAttribute(INFO_MESSAGE, infoMessage);
        }
        return "redirect:/oroscopo";
    }

    @GetMapping("/"+Constants.DOM_LUNA_SAPIENS_CONFIRM_EMAIL_OROSC_GIORN)
    public String confirmEmailOroscGiorn(@RequestParam(name = "code", required = true) String code, RedirectAttributes redirectAttributes) {
        logger.info("confirmEmailOroscGiorn endpoint");
        EmailUtenti emailUtenti = emailUtentiRepository.findByConfirmationCode( code ).orElse(null);
        String infoMessage = "";
        if(emailUtenti != null && emailUtenti.getConfirmationCode().trim().equals(code.trim())) {
            if(emailUtenti.getDataRegistrazione()== null){
                emailUtenti.setDataRegistrazione(new Date());
            }
            emailUtenti.setSubscription(true);
            emailUtentiRepository.save(emailUtenti);
            infoMessage = "Grazie per aver confermato la tua email. Sei ora iscritto al nostro servizio di oroscopo giornaliero con l'indirizzo "+emailUtenti.getEmail()+". " +
                    "Presto riceverai il tuo primo oroscopo nella tua casella di posta.";
        }else{
            infoMessage = "Conferma email non riuscita. Registrati di nuovo";
        }
        redirectAttributes.addFlashAttribute(INFO_MESSAGE, infoMessage);
        return "redirect:/oroscopo";
    }

    @GetMapping("/"+Constants.DOM_LUNA_SAPIENS_CANCELLA_ISCRIZ_OROSC_GIORN)
    public String cancelEmailOroscGiorn(@RequestParam(name = "code", required = true) String code, RedirectAttributes redirectAttributes) {
        logger.info("cancelEmailOroscGiorn code: "+code);
        EmailUtenti emailUtenti = emailUtentiRepository.findByConfirmationCode( code ).orElse(null);
        String infoMessage = "";
        if(emailUtenti != null && emailUtenti.getConfirmationCode().trim().equals(code.trim())) {
            if(emailUtenti.getDataRegistrazione()== null){
                emailUtenti.setDataRegistrazione(new Date());
            }
            emailUtenti.setSubscription(false);
            emailUtentiRepository.save(emailUtenti);
            infoMessage = "La tua cancellazione dall'Oroscopo del giorno è avvenuta con successo. Non riceverai più le nostre previsioni giornaliere. " +
                    "Se desideri iscriverti nuovamente in futuro, visita il nostro sito.";
        }else{
            infoMessage = "L'indirizzo email non è presente nel sistema.";
        }
        redirectAttributes.addFlashAttribute(INFO_MESSAGE, infoMessage);
        return "redirect:/oroscopo";
    }


    /**
     * trasmissione del video
     */
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
     * pagina contatti
     */
    @GetMapping("/contatti")
    public String contatti(Model model) {

            model.addAttribute("contactForm", new ContactFormDTO());

        return "contatti";
    }


    @PostMapping("/contattiSubmit")
    public String contattiSubmit(@Valid ContactFormDTO contactForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(INFO_ERROR, "Errore invio messaggio!");
            return "redirect:/error";
        }
        emailService.inviaEmailContatti(contactForm);
        redirectAttributes.addFlashAttribute(INFO_MESSAGE, "Messaggio inviato con successo!");
        return "redirect:/contatti";
    }




    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        model.addAttribute(INFO_ERROR, "Errore generale.");
        return "error";
    }

    @GetMapping("/info-privacy")
    public String infoPrivacy(Model model) { return "info-privacy"; }

    @GetMapping("/termini-di-servizio")
    public String terminiDiServizio(Model model) { return "termini-di-servizio"; }

    /**
     * lo uso solo per test
     */
    @GetMapping("/test-invia-email")
    public String inviaEmail(Model model) {

        /*
        EmailUtenti emailUtenti = new EmailUtenti();
        emailUtenti.setEmail("matteo.manili@gmail.com");
        emailService.inviaEmailOrosciopoGioraliero();
         */

        return "index";
    }

    /**
     * lo uso solo per test
     */
    @GetMapping("/genera-video")
    public String gerneraVideo(Model model) {
        //scheduledTasks.test_Oroscopo_Segni_Transiti_Aspetti();
        scheduledTasks.creaOroscopoGiornaliero();
        return "index";
    }

}
