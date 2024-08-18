package com.lunasapiens.filter;

import com.lunasapiens.Constants;
import com.lunasapiens.config.JwtElements;
import com.lunasapiens.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
    Ordine del Filtro: L'annotazione @Order(1) indica l'ordine di esecuzione del filtro. Questo può essere utile se ci sono più filtri nella tua applicazione e vuoi
    controllare l'ordine in cui vengono applicati.
 */

@Component
//@Order(1) // Ordine di esecuzione del filtro, se necessario (se ci sono altre classi che fanno da filter)
public class FilterRequestResponse extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(FilterRequestResponse.class);

    @Autowired
    JwtService jwtService;

    // TODO ricorda di rimettere MAX_REQUESTS a 10
    private static final int MAX_REQUESTS = 100; // Limite massimo di richieste per IP
    private Map<String, Integer> requestCounts = new HashMap<>();


    /**
     * se questi link vengono chiamati per più di Tot volte (MAX_REQUESTS) la applicazione blocca l'ip che richiama questi endpoint
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ipAddress = request.getRemoteAddr();

        logger.info("sono in FilterRequestResponse doFilterInternal");


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

        if (request.getRequestURI().equals("/contattiSubmit") && request.getMethod().equals("POST")) {
            handleMaxRequestRequest(request, response, ipAddress);
        }

        if (request.getRequestURI().equals("/test") && request.getMethod().equals("GET")) {
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

        // ######################### AUTENTICAZIONE JWT #########################
        String email = null;
        // Cerca il token nel cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    String token = cookie.getValue();

                    if(token != null){
                        JwtElements.JwtEmail jwtEmail = jwtService.validateTokenAndGetEmail(token);
                        email = jwtEmail.getEmail();
                        break;
                    }

                }
            }
        }


        /*
        // Il codice legge il token JWT dall'header Authorization e lo verifica.
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            email = jwtService.validateTokenAndGetEmail(token);
        }
         */

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Verifica se l'utente è già autenticato

            logger.info("eseguo la autenticazione: .setAuthentication(authentication)");

            UserDetails userDetails = User.withUsername(email).password("").authorities("USER").build();
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication); // Imposta l'autenticazione

        }




        filterChain.doFilter(request, response);
    }



    private void handleMaxRequestRequest(HttpServletRequest request, HttpServletResponse response, String ipAddress) throws IOException {
        if (!requestCounts.containsKey(ipAddress)) {
            requestCounts.put(ipAddress, 1);
        } else {
            int count = requestCounts.get(ipAddress);
            if (count >= MAX_REQUESTS) {
                // Imposta un flash attribute per indicare al controller di non salvare l'email
                request.setAttribute(Constants.SKIP_EMAIL_SAVE, true);
                response.setStatus(Constants.TOO_MANY_REQUESTS_STATUS_CODE);
                response.getWriter().write("Too many requests from this IP");
                response.getWriter().flush();
                return; // Termina il filtro senza tentare il redirect
            }
            // Incrementa il conteggio delle richieste per quell'indirizzo IP di 1
            requestCounts.put(ipAddress, count + 1);
        }
    }

}

