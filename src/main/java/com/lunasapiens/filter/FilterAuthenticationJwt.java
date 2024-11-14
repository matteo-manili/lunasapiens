package com.lunasapiens.filter;

import com.lunasapiens.Constants;
import com.lunasapiens.Utils;
import com.lunasapiens.config.JwtElements;
import com.lunasapiens.entity.ProfiloUtente;
import com.lunasapiens.repository.ProfiloUtenteRepository;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


/**
 Ordine del Filtro: L'annotazione @Order(1) indica l'ordine di esecuzione del filtro. Questo può essere utile se ci sono più filtri nella tua applicazione e vuoi
 controllare l'ordine in cui vengono applicati.
 */

@Component
@Order(2)  // Imposta un ordine preferito
public class FilterAuthenticationJwt extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(FilterAuthenticationJwt.class);

    @Autowired
    JwtService jwtService;

    @Autowired
    private ProfiloUtenteRepository profiloUtenteRepository;


    /**
     * se questi link vengono chiamati per più di Tot volte (MAX_REQUESTS) la applicazione blocca l'ip che richiama questi endpoint
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //logger.info("sono in FilterCheckJwtAuthentication doFilterInternal request.getRequestURI(): "+request.getRequestURI());

        // ######################### AUTENTICAZIONE JWT #########################

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getValue() != null && cookie.getName().equals(Constants.COOKIE_LUNASAPIENS_AUTH_TOKEN) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    logger.info("authenticationNow è null");
                    JwtElements.JwtDetails jwtDetails = jwtService.validateToken(cookie.getValue());
                    if (jwtDetails.isSuccess()) {
                        Optional<ProfiloUtente> profiloUtenteOpt = profiloUtenteRepository.findByEmail(jwtDetails.getSubject());
                        if (profiloUtenteOpt.isPresent()) {
                            autenticaUtente(profiloUtenteOpt.get().getEmail(), request);
                        }else{
                            Utils.clearJwtCookie_ClearSecurityContext(request, response);
                        }
                    } else {
                        // IMPORTANTE, cancellare il cookie altrimenti va il loop caricando sempre la pagina /register
                        Utils.clearJwtCookie_ClearSecurityContext(request, response);
                        if (jwtDetails.isTokenScaduto()) {
                            logger.info("token scaduto");
                            request.getSession().setAttribute(Constants.INFO_ALERT, Constants.MESSAGE_AUTENTICAZIONE_SCADUTA_INVIA_NUOVA_EMAIL);
                            response.sendRedirect("/register");

                        } else {
                            request.getSession().setAttribute(Constants.INFO_ERROR, jwtDetails.getMessaggioErroreJwt());
                            response.sendRedirect("/register");
                        }
                    }
                    break;
                }
            }
        }
        filterChain.doFilter(request, response);
    }


    public void autenticaUtente(String email, HttpServletRequest request) {
        logger.info("eseguo la autenticazione: "+email);
        UserDetails userDetails = User.withUsername(email)
                .password("").authorities( Constants.USER ).build();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // Imposta l'autenticazione nel contesto di sicurezza
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }



    public void autenticaUser(HttpServletRequest request) {

        String username = UUID.randomUUID().toString();

        logger.info("eseguo la autenticazione: "+username);
        UserDetails userDetails = User.withUsername(username)
                .password("").authorities( "ANONIMUS_BASE" ).build();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // Imposta l'autenticazione nel contesto di sicurezza
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


}