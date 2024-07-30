package com.lunasapiens.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunasapiens.*;
import com.lunasapiens.filter.RateLimiter;
import com.lunasapiens.dto.ContactFormDTO;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.dto.OroscopoDelGiornoDescrizioneDTO;
import com.lunasapiens.dto.OroscopoGiornalieroDTO;
import com.lunasapiens.entity.EmailUtenti;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.repository.EmailUtentiRepository;
import com.lunasapiens.service.OroscopoGiornalieroService;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;
import com.lunasapiens.zodiac.ServizioTemaNatale;
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
        model.addAttribute(INFO_MESSAGE, "Welcome to our dynamic landing page!");

        // div id="vypocty_id_nativ"

        String url = "https://horoscopes.astro-seek.com/calculate-birth-chart-horoscope-online/?input_natal=1" +
                "&send_calculation=1&narozeni_den=23" +
                "&narozeni_mesic=1&narozeni_rok=1981&narozeni_hodina=16" +
                "&narozeni_minuta=16" +
                "&narozeni_sekunda=00" +
                "&narozeni_city=Rome%2C+Italy" +
                "&narozeni_mesto_hidden=Rome" +
                "&narozeni_stat_hidden=IT" +
                "&narozeni_podstat_kratky_hidden=" +
                "&narozeni_sirka_stupne=41" +
                "&narozeni_sirka_minuty=54" +
                "&narozeni_sirka_smer=0" +
                "&narozeni_delka_stupne=12" +
                "&narozeni_delka_minuty=31" +
                "&narozeni_delka_smer=0" +
                "&narozeni_timezone_form=auto" +
                "&narozeni_timezone_dst_form=auto" +
                "&house_system=placidus" +
                "&hid_fortune=1" +
                "&hid_fortune_check=on" +
                "&hid_vertex=1" +
                "&hid_vertex_check=on" +
                "&hid_chiron=1&hid_chiron_check=on" +
                "&hid_lilith=1&hid_lilith_check=on" +
                "&hid_uzel=1&hid_uzel_check=on" +
                "&tolerance=1&aya=" +
                "&tolerance_paral=1.2#tabs_redraw";
        String html = restTemplate.getForObject(url, String.class);
        Document document = Jsoup.parse(html);

        // Estrai il contenuto del div con id "vypocty_id_nativ"
        Element divElement = document.getElementById("vypocty_id_nativ");

        if (divElement != null) {
            System.out.println("Contenuto del div 'vypocty_id_nativ':");

            System.out.println(divElement.html());

            // Seleziona tutti i blocchi di dati dei pianeti
            for (Element planetElement : divElement.select("div[style^=float: left; width: 80px; margin-left: -5px;]")) {
                String planetName = planetElement.select("a.tenky-modry").text();
                String signName = planetElement.nextElementSibling().select("img.astro_symbol").attr("alt");
                String position = planetElement.nextElementSibling().nextElementSibling().text();


                Element retrogradeElement = planetElement.nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling();
                boolean isRetrograde = retrogradeElement.text().trim().equals("R");

                double positionInDegrees = convertToDecimalDegrees(position);

                System.out.println("Pianeta: " + planetName);
                System.out.println("Segno: " + signName);
                System.out.println("Posizione: " + position + " (" + positionInDegrees + " gradi decimali)");

                System.out.println("Retrogrado: " + (isRetrograde ? "Sì" : "No"));

                System.out.println();
            }


            // TODO scrivi qui il codice per rfecuperare i valorti delle case astrologiche



            // Seleziona tutti i blocchi di dati delle case astrologiche
            //Elements houseElements = divElement.select("div[style^=float: left; width: 23px; font-size: 1.1em;]");
                                            //                 <div style="float: left; width: 20px; font-size: 1.1em;">

            for (Element houseElement : divElement.select("div[style^=float: left; width: 23px; font-size: 1.1em], div[style^=float: left; width: 20px; font-size: 1.1em]")) {
                String houseName = houseElement.text().replace(":", "");
                Element siblingElement = houseElement.nextElementSibling();
                String signName = siblingElement.select("img.astro_symbol").attr("alt");
                String position = siblingElement.nextElementSibling().text();

                double positionInDegrees = convertToDecimalDegrees(position);

                System.out.println("Casa: " + houseName);
                System.out.println("Segno: " + signName);
                System.out.println("Posizione: " + position + " (" + positionInDegrees + " gradi decimali)");
                System.out.println();
            }



        } else {
            System.out.println("Il div con id 'vypocty_id_nativ' non è stato trovato.");
        }
        return "index";
    }


    private double convertToDecimalDegrees(String position) {
        String[] parts = position.split("°|'");

        int degrees = Integer.parseInt(parts[0].trim());
        int minutes = Integer.parseInt(parts[1].replace("’", "").trim());

        return degrees + (minutes / 60.0);
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
        Optional.ofNullable(statoName).filter(state -> !state.isEmpty()).ifPresent(state -> model.addAttribute("statoName", state));
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
    public String temaNataleSubmit(@RequestParam("dateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime, @RequestParam("cityLat") String cityLat,
                                   @RequestParam("cityLng") String cityLng, @RequestParam("cityName") String cityName, @RequestParam("regioneName") String regioneName,
                                   @RequestParam("statoName") String statoName, Model model) {

        logger.info("sono in temaNataleSubmit");

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

        model.addAttribute("cityInput", cityName+", "+regioneName+", "+statoName);
        model.addAttribute("cityName", cityName);
        model.addAttribute("regioneName", regioneName);
        model.addAttribute("statoName", statoName);
        model.addAttribute("cityLat", cityLat);
        model.addAttribute("cityLng", cityLng);
        model.addAttribute("dateTime", datetime.format( Constants.DATE_TIME_LOCAL_FORMATTER ));
        model.addAttribute("dataOraNascita", datetime.format( Constants.DATE_TIME_FORMATTER ));
        model.addAttribute("luogoNascita", cityName+", "+regioneName+", "+statoName);
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = new GiornoOraPosizioneDTO(hour, minute, day, month, year, Double.parseDouble(cityLat), Double.parseDouble(cityLng));

        String temaNataleDescrizione = servizioTemaNatale.temaNataleDescrizione(giornoOraPosizioneDTO);
        model.addAttribute("temaNataleDescrizione", temaNataleDescrizione);

        String temaNataleId = UUID.randomUUID().toString();
        model.addAttribute("temaNataleId", temaNataleId);


        // Metto in cache i chatMessageIa
        Cache cache = cacheManager.getCache(Constants.TEMA_NATALE_BOT_CACHE);
        List<ChatMessage> chatMessageIa = new ArrayList<>();
        String temaNataleDescrizioneIstruzioneBOTSystem = "Rispondi alle domande dell'utente in base al suo tema natale: " + temaNataleDescrizione;
        chatMessageIa.add(new ChatMessage("system", temaNataleDescrizioneIstruzioneBOTSystem));
        cache.put(temaNataleId, chatMessageIa);

        return "tema-natale";
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
