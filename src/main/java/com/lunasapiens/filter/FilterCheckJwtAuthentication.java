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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Optional;


/**
 Ordine del Filtro: L'annotazione @Order(1) indica l'ordine di esecuzione del filtro. Questo può essere utile se ci sono più filtri nella tua applicazione e vuoi
 controllare l'ordine in cui vengono applicati.
 */

@Component
@Order(2) // Ordine di esecuzione del filtro, se necessario (se ci sono altre classi che fanno da filter)
public class FilterCheckJwtAuthentication extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(FilterCheckJwtAuthentication.class);

    @Autowired
    JwtService jwtService;

    @Autowired
    private ProfiloUtenteRepository profiloUtenteRepository;

    /**
     * se questi link vengono chiamati per più di Tot volte (MAX_REQUESTS) la applicazione blocca l'ip che richiama questi endpoint
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //logger.info("sono in FilterCheckJwtAuthentication doFilterInternal");

        // ######################### AUTENTICAZIONE JWT #########################
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Constants.COOKIE_JWT_NAME.equals(cookie.getName()) && cookie.getValue() != null) {
                    JwtElements.JwtDetails jwtDetails = jwtService.validateToken(cookie.getValue());
                    if (jwtDetails.isSuccess()) {
                        autenticaUtente(jwtDetails, request, response);
                    } else {
                        if (jwtDetails.isTokenScaduto()) {
                            logger.info("token scaduto");
                            request.getSession().setAttribute(Constants.INFO_ERROR, "Link di autenticazione scaduto. Ripetere l'autenticazione.");
                        } else {
                            request.getSession().setAttribute(Constants.INFO_ERROR, jwtDetails.getMessaggioErroreJwt());
                        }
                        Utils.clearJwtCookie_ClearSecurityContext(request, response);
                        response.sendRedirect("/register");
                    }
                    break;
                }
            }
        }

        filterChain.doFilter(request, response);
    }



    public void autenticaUtente(JwtElements.JwtDetails jwtDetails, HttpServletRequest request, HttpServletResponse response) {
        Authentication authenticationNow = SecurityContextHolder.getContext().getAuthentication();
        if (authenticationNow == null) {
            Optional<ProfiloUtente> profiloUtenteOpt = profiloUtenteRepository.findByEmail(jwtDetails.getSubject());
            if (profiloUtenteOpt.isPresent()) {
                logger.info("eseguo la autenticazione: .setAuthentication(authentication)");
                UserDetails userDetails = User.withUsername(profiloUtenteOpt.get().getEmail())
                        .password("")
                        .authorities("USER")
                        .build();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Imposta l'autenticazione nel contesto di sicurezza
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // controllo che la sessione attiva sia effettivamente quella dell'utente del token altrimenti la cancello
        } else if (authenticationNow != null && authenticationNow.isAuthenticated()
                && !authenticationNow.getName().equals(jwtDetails.getSubject())) {
            logger.info("authenticationNow.getName(): " + authenticationNow.getName());
            Utils.clearJwtCookie_ClearSecurityContext(request, response);
        }
    }

}