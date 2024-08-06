package com.lunasapiens.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunasapiens.*;
import com.lunasapiens.dto.*;
import com.lunasapiens.filter.RateLimiter;
import com.lunasapiens.zodiac.BuildInfoAstrologiaAstroSeek;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;
import com.lunasapiens.zodiac.ServizioTemaNatale;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;


@Controller
public class TemaNataleController {

    private static final Logger logger = LoggerFactory.getLogger(TemaNataleController.class);

    @Autowired
    private String getApiGeonamesUsername;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ServizioOroscopoDelGiorno servizioOroscopoDelGiorno;

    @Autowired
    ServizioTemaNatale servizioTemaNatale;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RateLimiter rateLimiter;

    @Autowired
    private TelegramBotClient telegramBotClient;


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

        String luogoNascita = String.join(", ", cityName, regioneName, statoName);

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
        if (cache == null) {
            logger.error("Cache not found: " + Constants.TEMA_NATALE_BOT_CACHE);
            return "redirect:/tema-natale";
        }
        List<ChatMessage> chatMessageIa = new ArrayList<>();
        String temaNataleDescrizioneIstruzioneBOTSystem = BuildInfoAstrologiaAstroSeek
                .temaNataleIstruzioneBOTSystem(temaNataleDescrizione, datetime, luogoNascita);
        logger.info( "temaNataleDescrizioneIstruzioneBOTSystem: "+temaNataleDescrizioneIstruzioneBOTSystem );
        chatMessageIa.add(new ChatMessage("system", temaNataleDescrizioneIstruzioneBOTSystem));
        cache.put(temaNataleId, chatMessageIa);

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



}
