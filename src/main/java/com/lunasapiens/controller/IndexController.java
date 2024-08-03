package com.lunasapiens.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunasapiens.*;
import com.lunasapiens.config.AppConfig;
import com.lunasapiens.dto.*;
import com.lunasapiens.filter.RateLimiter;
import com.lunasapiens.entity.EmailUtenti;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.repository.EmailUtentiRepository;
import com.lunasapiens.service.OroscopoGiornalieroService;
import com.lunasapiens.zodiac.*;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import com.theokanning.openai.completion.chat.ChatMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
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

    @Autowired
    private RateLimiter rateLimiter;

    @Autowired
    private TelegramBotClient telegramBotClient;

    private OroscopoGiornalieroService oroscopoGiornalieroService;
    @Autowired
    public IndexController(OroscopoGiornalieroService oroscopoGiornalieroService) {
        this.oroscopoGiornalieroService = oroscopoGiornalieroService;
    }

    private static final String INFO_MESSAGE = "infoMessage";
    private static final String INFO_ERROR = "infoError";


    @GetMapping("/cattura_seek")
    public String catturaTemaNataleAstroSeek(Model model) {

        return "index";
    }






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
                             @ModelAttribute("temaNataleId") String temaNataleId
    ) {

        logger.info("sono in temaNatale");
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
        Optional.ofNullable(temaNataleId).filter(id -> !id.isEmpty()).ifPresent(id -> model.addAttribute("temaNataleId", id));

        logger.info("Tema Natale ID: " + model.getAttribute("temaNataleId"));

        // ############ Dati per test, togliere poi!!! ############
        //model.addAttribute("temaNataleDescrizione", "ciao bello ciao bello ciao bello ciao bello ciao bello <br> ciao bello ciao bello <br>ciao bello ciao bello <br>ciao bello ciao bello <br>ciao bello ciao bello <br>ciao bello ciao bello <br>");
        /*
        model.addAttribute("cityInput","Roma, Lazio, Italia");
        model.addAttribute("cityName", "Roma");
        model.addAttribute("regioneName", "Lazio");
        model.addAttribute("statoName", "Italia");
        model.addAttribute("cityLat", "41.89193");
        model.addAttribute("cityLng", "12.51133");
        model.addAttribute("luogoNascita", "Roma, Lazio, Italia");
        */
        return "tema-natale";
    }

    @GetMapping("/temaNataleSubmit")
    public String temaNataleSubmit(@RequestParam("dateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime,
                                   @RequestParam("cityLat") String cityLat,
                                   @RequestParam("cityLng") String cityLng,
                                   @RequestParam("cityName") String cityName,
                                   @RequestParam("regioneName") String regioneName,
                                   @RequestParam("statoName") String statoName,
                                   @RequestParam("statoCode") String statoCode,
                                   RedirectAttributes redirectAttributes) {

        logger.info("sono in temaNataleSubmit");
        // Estrai le singole componenti della data e ora
        int hour = datetime.getHour();
        int minute = datetime.getMinute();
        int day = datetime.getDayOfMonth();
        int month = datetime.getMonthValue();
        int year = datetime.getYear();

        String luogoNascita = cityName + ", " + regioneName + ", " + statoName;

        logger.info("Ora: " + hour);
        logger.info("Minuti: " + minute);
        logger.info("Giorno: " + day);
        logger.info("Mese: " + month);
        logger.info("Anno: " + year);
        logger.info("cityName: " + cityName);
        logger.info("regioneName: " + regioneName);
        logger.info("statoName: " + statoName);
        logger.info("statoCode: " + statoCode);
        logger.info("Latitude: " + cityLat);
        logger.info("Longitude: " + cityLng);

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


        GiornoOraPosizioneDTO giornoOraPosizioneDTO = new GiornoOraPosizioneDTO(hour, minute, day, month, year, Double.parseDouble(cityLat), Double.parseDouble(cityLng));
        CoordinateDTO coordinateDTO = new CoordinateDTO(cityName, regioneName, statoName, statoCode);

        //String temaNataleDescrizione = servizioTemaNatale.temaNataleDescrizione_AstrologiaSwiss(giornoOraPosizioneDTO);

        String temaNataleDescrizione = servizioTemaNatale.temaNataleDescrizione_AstrologiaAstroSeek(giornoOraPosizioneDTO, coordinateDTO);


        redirectAttributes.addFlashAttribute("temaNataleDescrizione", temaNataleDescrizione);

        String temaNataleId = UUID.randomUUID().toString();
        redirectAttributes.addFlashAttribute("temaNataleId", temaNataleId);

        // Metto in cache i chatMessageIa
        Cache cache = cacheManager.getCache(Constants.TEMA_NATALE_BOT_CACHE);
        List<ChatMessage> chatMessageIa = new ArrayList<>();

        String temaNataleDescrizioneIstruzioneBOTSystem = BuildInfoAstrologiaAstroSeek
                .temaNataleIstruzioneBOTSystem(temaNataleDescrizione, datetime, luogoNascita);

        logger.info( "temaNataleDescrizioneIstruzioneBOTSystem: "+temaNataleDescrizioneIstruzioneBOTSystem );


        chatMessageIa.add(new ChatMessage("system", temaNataleDescrizioneIstruzioneBOTSystem));
        cache.put(temaNataleId, chatMessageIa);

        // Redirect alla pagina 'tema-natale'
        return "redirect:/tema-natale";
    }

    /**
     * Spring WebSocket
     * In questa modalità invia il messaggio a tutti gli utenti cioè a tuitti i browser: @MessageMapping("/message") @SendTo("/topic/messages")
     */
    @MessageMapping("/message")
    @SendToUser("/queue/reply")
    public Map<String, Object> userMessageWebSocket(Map<String, String> message, Principal principal) {
        String userId = principal.getName();
        Map<String, Object> response = new HashMap<>();

        // Controlla il limite di frequenza dei messaggi
        if (!rateLimiter.allowMessage(userId)) {
            response.put("error", "Troppi messaggi! Per favore attendi.");
            return response;
        }

        String domanda = message.get("content");
        String temaNataleId = message.get("temaNataleId");

        // Aggiunge una protezione per i dati nulli o non validi
        if (domanda == null || domanda.isEmpty()) {
            response.put("error", "Il messaggio non può essere vuoto.");
            return response;
        }

        Cache cache = cacheManager.getCache(Constants.TEMA_NATALE_BOT_CACHE);
        if (cache != null) {
            // Recupera la lista di chat messages dalla cache
            List<ChatMessage> chatMessageIa = cache.get(temaNataleId, List.class);
            if (chatMessageIa == null) {
                chatMessageIa = new ArrayList<>();
            }

            chatMessageIa.add(new ChatMessage("user", HtmlUtils.htmlEscape(domanda)));
            cache.put(temaNataleId, chatMessageIa);

            try {
                StringBuilder rispostaIA = servizioTemaNatale.chatBotTemaNatale(chatMessageIa);
                chatMessageIa.add(new ChatMessage("assistant", rispostaIA.toString()));
                cache.put(temaNataleId, chatMessageIa);

                response.put("content", rispostaIA.toString());
                telegramBotClient.inviaMessaggio("Domanda Utente: " + domanda);
            } catch (Exception e) {
                response.put("error", "Errore durante l'elaborazione: " + e.getMessage());
            }
        } else {
            response.put("error", "Cache non trovata.");
        }

        return response;
    }




    @GetMapping("/coordinate")
    public ResponseEntity<Object> getCoordinates(@RequestParam String cityName) {
        String url = "http://api.geonames.org/searchJSON?name_startsWith=" + cityName + "&username=" + getApiGeonamesUsername + "&style=MEDIUM&lang=it&maxRows=5";
        logger.info(url);
        String response;
        List<Map<String, Object>> locations = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        try {
            response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode geonames = root.path("geonames");
            for (JsonNode node : geonames) {
                if ("P".equals(node.path("fcl").asText())) {
                    String name = node.path("name").asText();
                    String adminName1 = node.path("adminName1").asText();
                    String countryCode = node.path("countryCode").asText();
                    String uniqueKey = name + "|" + adminName1 + "|" + countryCode;
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
            return new ResponseEntity<>(locations, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Errore durante il recupero delle coordinate", e);
            // Restituisci la pagina di errore con il messaggio
            ModelAndView modelAndView = new ModelAndView("error");
            modelAndView.addObject("infoError", "Si è verificato un errore: " + e.getMessage());
            return new ResponseEntity<>(modelAndView.getModel(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
    public String pageError(HttpServletRequest request, Model model) {
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

    @GetMapping("/")
    public String index(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute(INFO_MESSAGE, "Welcome to our dynamic landing page!");
        return "index";
    }



    @GetMapping("/robots.txt")
    public void getRobotsTxt(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        writer.println("User-agent: *");
        for(String url : Constants.URL_NO_INDEX_LIST){
            writer.println("Disallow: "+url);
        }
        writer.println();
        writer.println("Sitemap: "+Constants.DOM_LUNA_SAPIENS+"/"+"sitemap.xml");
        writer.close();
    }

    @GetMapping("/sitemap.xml")
    public void getSitemap(HttpServletResponse response) throws IOException {
        // Crea il generatore di sitemap
        WebSitemapGenerator sitemapGenerator = WebSitemapGenerator.builder(Constants.DOM_LUNA_SAPIENS, new File(".")).build();

        // Aggiungi URL alla sitemap
        List<String> urlsForIndex = List.of(
                "/",
                "/oroscopo",
                "/tema-natale",
                "/contatti"
        );

        for (String url : urlsForIndex) {
            WebSitemapUrl sitemapUrl = new WebSitemapUrl.Options(Constants.DOM_LUNA_SAPIENS + url).build();
            sitemapGenerator.addUrl(sitemapUrl);
        }

        // Genera la sitemap
        List<String> sitemapUrls = sitemapGenerator.writeAsStrings();

        // Imposta il tipo di contenuto e restituisci la sitemap
        response.setContentType("application/xml");
        response.getWriter().write(String.join("\n", sitemapUrls));
    }

}
