package com.lunasapiens.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
    Ordine del Filtro: L'annotazione @Order(1) indica l'ordine di esecuzione del filtro. Questo può essere utile se ci sono più filtri nella tua applicazione e vuoi
    controllare l'ordine in cui vengono applicati.
 */

@Component
@Order(1)
public class FilterCSRF extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Recupera il token CSRF dall'attributo della richiesta
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        // Se il token CSRF è presente, aggiungilo nell'intestazione della risposta
        if (csrf != null) {
            response.setHeader("X-CSRF-TOKEN", csrf.getToken());
        }

        // Continua la catena di filtri
        filterChain.doFilter(request, response);
    }




}

