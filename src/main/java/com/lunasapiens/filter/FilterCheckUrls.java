package com.lunasapiens.filter;

import com.lunasapiens.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
    Ordine del Filtro: L'annotazione @Order(1) indica l'ordine di esecuzione del filtro. Questo può essere utile se ci sono più filtri nella tua applicazione e vuoi
    controllare l'ordine in cui vengono applicati.
 */

@Component
@Order(2)
public class FilterCheckUrls extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(FilterCheckUrls.class);


    // TODO ricorda di rimettere MAX_REQUESTS a 10
    private static final int MAX_REQUESTS = 10; // Limite massimo di richieste per IP
    private Map<String, Integer> requestCounts = new HashMap<>();

    private Set<String> blockedIps = new HashSet<>();


    /**
     * se questi link vengono chiamati per più di Tot volte (MAX_REQUESTS) la applicazione blocca l'ip che richiama questi endpoint
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ipAddress = request.getRemoteAddr();

        //logger.info("sono in FilterCheckUrls doFilterInternal");
        //logger.info("Richiesta da IP: {} per URI: {}", ipAddress, request.getRequestURI());

        if (blockedIps.contains(ipAddress)) {
            logger.warn("Bloccato IP: {} per URI: {}", ipAddress, request.getRequestURI());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }


        // ######################### Controllo MaxRequest #########################
        if (request.getRequestURI().equals("/"+ Constants.DOM_LUNA_SAPIENS_SUBSCRIBE_OROSC_GIORN) && request.getMethod().equals("POST")) {
            handleMaxRequestRequest(request, response, ipAddress);
        }

        if (request.getRequestURI().equals("/"+Constants.DOM_LUNA_SAPIENS_CONFIRM_EMAIL_OROSC_GIORN) && request.getMethod().equals("GET")) {
            handleMaxRequestRequest(request, response, ipAddress);
        }

        if (request.getRequestURI().equals("/"+Constants.DOM_LUNA_SAPIENS_CANCELLA_ISCRIZ_OROSC_GIORN) && request.getMethod().equals("GET")) {
            handleMaxRequestRequest(request, response, ipAddress);
        }

        if (request.getRequestURI().equals("/registrazioneUtente") && request.getMethod().equals("POST")) {
            handleMaxRequestRequest(request, response, ipAddress);
        }

        if (request.getRequestURI().equals("/contattiSubmit") && request.getMethod().equals("POST")) {
            logger.info("Richiesta sospetta da IP: {} per URI: {} - User Agent: {}",
                    ipAddress, request.getRequestURI(), request.getHeader("User-Agent"));
            handleMaxRequestRequest(request, response, ipAddress);
        }

        if (request.getRequestURI().equals("/genera-video") && request.getMethod().equals("GET")) {
            handleMaxRequestRequest(request, response, ipAddress);
        }


        // ######################### url no index #########################
        for (String urlNoIndex : Constants.URL_NO_INDEX_STATUS_410_LIST) {
            if (request.getRequestURI().equals( urlNoIndex )) {
                response.setStatus(HttpStatus.GONE.value());  // Imposta il codice di stato a 410
                response.setHeader("X-Robots-Tag", "noindex, nofollow");
            }
        }


        filterChain.doFilter(request, response);
    }



    private void handleMaxRequestRequest(HttpServletRequest request, HttpServletResponse response, String ipAddress) throws IOException {
        //logger.info("Richiesta da IP: {} per URI: {}", ipAddress, request.getRequestURI());
        if (!requestCounts.containsKey(ipAddress)) {
            requestCounts.put(ipAddress, 1);
        } else {
            int count = requestCounts.get(ipAddress);
            if (count >= MAX_REQUESTS) {
                blockedIps.add(ipAddress);
                logger.warn("IP aggiunto alla blacklist: {}", ipAddress);
                // Imposta un flash attribute per indicare al controller di non salvare l'email
                request.setAttribute(Constants.SKIP_EMAIL_SAVE, true);
                response.setStatus(Constants.TOO_MANY_REQUESTS_STATUS_CODE);
                // Verifica se la risposta è già stata commessa prima di scrivere sulla risposta
                if (!response.isCommitted()) {
                    response.getWriter().write("Too many requests from this IP");
                    response.getWriter().flush();
                }
                return; // Termina il filtro senza tentare il redirect
            }
            // Incrementa il conteggio delle richieste per quell'indirizzo IP di 1
            requestCounts.put(ipAddress, count + 1);
        }
    }

}

