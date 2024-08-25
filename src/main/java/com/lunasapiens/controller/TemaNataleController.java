package com.lunasapiens.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunasapiens.*;
import com.lunasapiens.config.ApiGeonamesConfig;
import com.lunasapiens.config.CustomPrincipalWebSocket;
import com.lunasapiens.config.WebSocketConfig;
import com.lunasapiens.dto.*;
import com.lunasapiens.entity.ProfiloUtente;
import com.lunasapiens.filter.RateLimiterUser;
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
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.*;


@Controller
public class TemaNataleController {

    private static final Logger logger = LoggerFactory.getLogger(TemaNataleController.class);

    @Autowired
    private ApiGeonamesConfig getApiGeonames;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ServizioOroscopoDelGiorno servizioOroscopoDelGiorno;

    @Autowired
    ServizioTemaNatale servizioTemaNatale;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RateLimiterUser rateLimiterUser;

    @Autowired
    private TelegramBotClient telegramBotClient;

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
                             @ModelAttribute(Constants.USER_SESSION_ID) String userSessionId
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
        Optional.ofNullable(paginaChatId).filter(id -> !id.isEmpty()).ifPresent(id -> model.addAttribute("paginaChatId", id));
        Optional.ofNullable(userSessionId).filter(id -> !id.isEmpty()).ifPresent(id -> model.addAttribute(Constants.USER_SESSION_ID, id));

        logger.info("Tema Natale ID: " + model.getAttribute("paginaChatId"));
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






    /**
     * Spring WebSocket
     * In questa modalità invia il messaggio a tutti gli utenti cioè a tuitti i browser: @MessageMapping("/message") @SendTo("/topic/messages")
     */
    @MessageMapping("/message")
    @SendToUser("/queue/reply")
    public Map<String, Object> userMessageWebSocketTemaNatale(Map<String, String> message, CustomPrincipalWebSocket principal) {
        logger.info("sono in userMessageWebSocketTemaNatale");
        return responseChatBot(message, principal);
    }



    public Map<String, Object> responseChatBot( Map<String, String> message, CustomPrincipalWebSocket principal ){

        Map<String, Object> response = new HashMap<>(); final String keyJsonStandardContent = "content";
        CustomPrincipalWebSocket customPrincipalWebSocket = (CustomPrincipalWebSocket) principal;
        if( customPrincipalWebSocket != null ){
            String domanda = message.get( keyJsonStandardContent );
            String paginaChatId = message.get("paginaChatId");
            String userSessionId = message.get(Constants.USER_SESSION_ID);

            logger.info("customPrincipal.getIpAddress(): "+customPrincipalWebSocket.getIpAddress());
            logger.info("customPrincipal.getName(): "+customPrincipalWebSocket.getName());
            logger.info("domanda: "+domanda);

            if (domanda == null || domanda.isEmpty()) {
                response.put(keyJsonStandardContent, "Il messaggio non può essere vuoto.");
                return response;
            }
            if( customPrincipalWebSocket.getName().startsWith(WebSocketConfig.userAnonymous) ){
                logger.info("User not logged in");
                if (!rateLimiterUser.allowMessage( customPrincipalWebSocket.getIpAddress(), RateLimiterUser.MAX_MESSAGES_PER_DAY_ANONYMOUS )) {
                    response.put(keyJsonStandardContent, rateLimiterUser.numeroMessaggi_e_Minuti( RateLimiterUser.MAX_MESSAGES_PER_DAY_ANONYMOUS)
                            + "<br>" + "<a href=\"/register\">Iscriviti</a> per fare più domande!") ;
                    return response;
                }
            }else{
                if (!rateLimiterUser.allowMessage( customPrincipalWebSocket.getIpAddress(), RateLimiterUser.MAX_MESSAGES_PER_DAY_UTENTE )) {
                    response.put(keyJsonStandardContent, rateLimiterUser.numeroMessaggi_e_Minuti( RateLimiterUser.MAX_MESSAGES_PER_DAY_UTENTE ) );
                    return response;
                }
            }

            Cache cache = cacheManager.getCache(Constants.MESSAGE_BOT_CACHE);
            if (cache != null ) {
                // Recupera la lista di chat messages dalla cache
                List<ChatMessage> chatMessageIa = cache.get(paginaChatId, List.class);
                if (chatMessageIa == null) {
                    chatMessageIa = new ArrayList<>();
                }
                chatMessageIa.add(new ChatMessage("user", HtmlUtils.htmlEscape(domanda)));
                cache.put(paginaChatId, chatMessageIa);
                try {
                    StringBuilder rispostaIA = servizioTemaNatale.chatBotTemaNatale(chatMessageIa);
                    //StringBuilder rispostaIA = new StringBuilder("risposta dalla iaaaaaaaaaaaaaaaaaaaa");

                    chatMessageIa.add(new ChatMessage("assistant", rispostaIA.toString()));
                    cache.put(paginaChatId, chatMessageIa);
                    response.put(keyJsonStandardContent, rispostaIA.toString());

                    if ( customPrincipalWebSocket.getName().startsWith(WebSocketConfig.userAnonymous) ) {
                        telegramBotClient.inviaMessaggio("user: "+domanda);
                    }else{
                        telegramBotClient.inviaMessaggio("utente: "+domanda);
                    }
                } catch (Exception e) {
                    response.put(keyJsonStandardContent, "Errore durante l'elaborazione: " + e.getMessage());
                }
            } else {
                response.put(keyJsonStandardContent, "Errore durante l'elaborazione.");
            }
        }

        return response;


    }



    @GetMapping("/coordinate")
    public ResponseEntity<Object> getCoordinates(@RequestParam String cityName) {
        String url = "http://api.geonames.org/searchJSON?name_startsWith=" + cityName + "&username=" + getApiGeonames.getUsername() + "&style=MEDIUM&lang=it&maxRows=3";
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
