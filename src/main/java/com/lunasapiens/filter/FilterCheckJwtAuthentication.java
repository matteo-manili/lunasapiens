package com.lunasapiens.filter;

import com.lunasapiens.Constants;
import com.lunasapiens.Utils;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


/**
    Ordine del Filtro: L'annotazione @Order(1) indica l'ordine di esecuzione del filtro. Questo può essere utile se ci sono più filtri nella tua applicazione e vuoi
    controllare l'ordine in cui vengono applicati.
 */

@Component
@Order(1) // Ordine di esecuzione del filtro, se necessario (se ci sono altre classi che fanno da filter)
public class FilterCheckJwtAuthentication extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(FilterCheckJwtAuthentication.class);

    @Autowired
    JwtService jwtService;


    /**
     * se questi link vengono chiamati per più di Tot volte (MAX_REQUESTS) la applicazione blocca l'ip che richiama questi endpoint
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        logger.info("sono in FilterCheckJwtAuthentication doFilterInternal");



        // ######################### AUTENTICAZIONE JWT #########################
        Cookie[] cookies = request.getCookies();



        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Constants.COOKIE_JWT_NAME.equals(cookie.getName()) && cookie.getValue() != null ) {
                    Authentication authenticationNow = SecurityContextHolder.getContext().getAuthentication();
                    boolean isAuthenticated = authenticationNow != null && authenticationNow.isAuthenticated();
                    JwtElements.JwtDetails jwtDetails = jwtService.validateToken( cookie.getValue() );
                    if (jwtDetails.isSuccess() ) {
                        if( !isAuthenticated ) { // per non fare fare sempre la autenticazione - Verifica se l'utente è già autenticato
                            logger.info("eseguo la autenticazione: .setAuthentication(authentication)");
                            UserDetails userDetails = User.withUsername( jwtDetails.getSubject() ).password("").authorities("USER").build();
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication); // Imposta l'autenticazione
                        }
                    }else {
                        if( jwtDetails.isTokenScaduto() ) {
                            logger.info("token scaduto");
                            // Imposta un attributo nella sessione
                            request.getSession().setAttribute(Constants.INFO_ERROR, "Link di autenticazione scaduto. Ripetere l'autenticazione.");
                        }else{
                            // Imposta un attributo nella sessione
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



}

