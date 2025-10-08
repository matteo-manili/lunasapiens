package com.lunasapiens.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunasapiens.Constants;
import com.lunasapiens.service.RAGIAService;
import com.lunasapiens.service.TelegramBotService;
import com.lunasapiens.config.ApiGeonamesConfig;
import com.lunasapiens.config.CustomPrincipalWebSocketChatBot;
import com.lunasapiens.config.WebSocketConfig;
import com.lunasapiens.filter.RateLimiterUser;
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
public class WebSocket_e_ApiController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocket_e_ApiController.class);

    @Autowired
    private ApiGeonamesConfig getApiGeonames;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    ServizioTemaNatale servizioTemaNatale;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RateLimiterUser rateLimiterUser;

    @Autowired
    private TelegramBotService telegramBotService;

    @Autowired
    private RAGIAService rAGIAService;




    /**
     * Spring WebSocket
     * In questa modalità invia il messaggio a tutti gli utenti cioè a tuitti i browser: @MessageMapping("/message") @SendTo("/topic/messages")
     */
    @MessageMapping("/message")
    @SendToUser("/queue/reply")
    public Map<String, Object> userMessageWebSocket(Map<String, String> message, CustomPrincipalWebSocketChatBot principal) {
        logger.info("sono in userMessageWebSocket");
        return responseChatBot(message, principal);
    }



    public Map<String, Object> responseChatBot( Map<String, String> message, CustomPrincipalWebSocketChatBot principal ){
        Map<String, Object> response = new HashMap<>(); final String keyJsonStandardContent = "content";
        CustomPrincipalWebSocketChatBot customPrincipalWebSocketChatBot = (CustomPrincipalWebSocketChatBot) principal;
        if( customPrincipalWebSocketChatBot != null ){
            String domanda = message.get( keyJsonStandardContent ); String paginaChatId = message.get("paginaChatId");
            String userSessionId = message.get(Constants.USER_SESSION_ID);

            logger.info("customPrincipal.getIpAddress(): "+ customPrincipalWebSocketChatBot.getIpAddress());
            logger.info("customPrincipal.getName(): "+ customPrincipalWebSocketChatBot.getName());
            logger.info("domanda: "+domanda);


            Cache cacheMessageBot = cacheManager.getCache(Constants.MESSAGE_BOT_CACHE);
            if (cacheMessageBot != null ) {
                // Recupera la lista di chat messages dalla cache
                List<ChatMessage> chatMessageIaList = cacheMessageBot.get(paginaChatId, List.class);
                if (chatMessageIaList == null) {
                    chatMessageIaList = new ArrayList<>();

                }else if( chatMessageIaList.size() >= 2 ){
                    ChatMessage ultimoMessaggioUser = chatMessageIaList.get(chatMessageIaList.size() - 2);
                    if( ultimoMessaggioUser.getContent().equals(domanda) ){
                        response.put(keyJsonStandardContent, "Hai già fatto questa domanda." );
                        return response;
                    }
                }
                if( customPrincipalWebSocketChatBot.getName().startsWith(WebSocketConfig.userAnonymous) && message.get("tipoServizio").equals("SINASTRIA") ){
                    response.put(keyJsonStandardContent, "<a href=\"/register\">Iscriviti</a> per usare la ChatBot Sinastria IA");
                    return response;
                }
                if (domanda == null || domanda.isEmpty()) {
                    response.put(keyJsonStandardContent, "Il messaggio non può essere vuoto.");
                    return response;
                }
                if( customPrincipalWebSocketChatBot.getName().startsWith(WebSocketConfig.userAnonymous) ){
                    response.put( "numDomandeRimanenti", rateLimiterUser.getRemainingMessages( customPrincipalWebSocketChatBot.getIpAddress(), RateLimiterUser.MAX_MESSAGES_PER_DAY_ANONYMOUS ) );
                    if (!rateLimiterUser.allowMessage( customPrincipalWebSocketChatBot.getIpAddress(), RateLimiterUser.MAX_MESSAGES_PER_DAY_ANONYMOUS )) {
                        response.put(keyJsonStandardContent, rateLimiterUser.numeroMessaggi_e_Minuti(RateLimiterUser.MAX_MESSAGES_PER_DAY_ANONYMOUS)
                                +" "+ "<a href=\"/register\">Iscriviti</a> per fare più domande!") ;
                        return response;
                    }
                }else{
                    response.put( "numDomandeRimanenti", rateLimiterUser.getRemainingMessages( customPrincipalWebSocketChatBot.getIpAddress(), RateLimiterUser.MAX_MESSAGES_PER_DAY_UTENTE ) );
                    if (!rateLimiterUser.allowMessage( customPrincipalWebSocketChatBot.getIpAddress(), RateLimiterUser.MAX_MESSAGES_PER_DAY_UTENTE )) {
                        response.put(keyJsonStandardContent, rateLimiterUser.numeroMessaggi_e_Minuti( RateLimiterUser.MAX_MESSAGES_PER_DAY_UTENTE ) );
                        return response;
                    }
                }

                //PSICOLOGO

                try {
                    StringBuilder rispostaIA;
                    if( message.get("tipoServizio").equals("SINASTRIA") || message.get("tipoServizio").equals("SINASTRIA") ){
                        chatMessageIaList.add(new ChatMessage("user", domanda));
                        cacheMessageBot.put(paginaChatId, chatMessageIaList);
                        rispostaIA = servizioTemaNatale.chatBotTemaNatale(chatMessageIaList);


                    }else if (message.get("tipoServizio").equals("PSICOLOGO") ){
                        StringBuilder context = rAGIAService.getChunksContext(HtmlUtils.htmlEscape(domanda));
                        chatMessageIaList.add(new ChatMessage("system", "Informazioni: "+context.toString()));
                        chatMessageIaList.add(new ChatMessage("user", domanda));
                        cacheMessageBot.put(paginaChatId, chatMessageIaList);
                        rispostaIA = rAGIAService.chiediAlloPsicologo( chatMessageIaList, 0.0, 1000 );

                    }else{
                        return response;
                    }


                    //StringBuilder rispostaIA = new StringBuilder("risposta dalla iaaaaaaaaaaaaaaaaaaaa");

                    chatMessageIaList.add(new ChatMessage("assistant", rispostaIA.toString()));
                    cacheMessageBot.put(paginaChatId, chatMessageIaList);
                    response.put(keyJsonStandardContent, rispostaIA.toString());

                    if ( customPrincipalWebSocketChatBot.getName().startsWith(WebSocketConfig.userAnonymous) ) {
                        telegramBotService.inviaMessaggio("user: "+domanda);
                    }else{
                        telegramBotService.inviaMessaggio("utente: "+domanda);
                    }
                } catch (Exception e) {
                    response.put(keyJsonStandardContent, "Errore durante l'elaborazione: " + e.getMessage());
                }
            } else {
                response.put(keyJsonStandardContent, "Errore durante l'elaborazione.");
            }
        }else{
            response.put(keyJsonStandardContent, "Utente non riconosciuto");
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
            modelAndView.addObject(Constants.INFO_ERROR, "Si è verificato un errore nel recupero coordinate: " + e.getMessage());
            return new ResponseEntity<>(modelAndView.getModel(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}
