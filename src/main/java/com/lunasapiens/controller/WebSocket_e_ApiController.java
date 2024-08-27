package com.lunasapiens.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunasapiens.Constants;
import com.lunasapiens.TelegramBotClient;
import com.lunasapiens.config.ApiGeonamesConfig;
import com.lunasapiens.config.CustomPrincipalWebSocket;
import com.lunasapiens.config.WebSocketConfig;
import com.lunasapiens.filter.RateLimiterUser;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;
import com.lunasapiens.zodiac.ServizioTemaNatale;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;
import reactor.core.publisher.Mono;

import java.util.*;


@Controller
public class WebSocket_e_ApiController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocket_e_ApiController.class);

    @Autowired
    private ApiGeonamesConfig getApiGeonames;

    @Autowired
    private WebClient.Builder webClientBuilder;

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




    /**
     * Spring WebSocket
     * In questa modalità invia il messaggio a tutti gli utenti cioè a tuitti i browser: @MessageMapping("/message") @SendTo("/topic/messages")
     */
    @MessageMapping("/message")
    @SendToUser("/queue/reply")
    public Map<String, Object> userMessageWebSocket(Map<String, String> message, CustomPrincipalWebSocket principal) {
        logger.info("sono in userMessageWebSocket");
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
        List<Map<String, Object>> locations = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        try {
            WebClient webClient = webClientBuilder.build();
            Mono<String> responseMono = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class);
            String response = responseMono.block(); // Note: Using block() is not recommended for non-blocking code


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
