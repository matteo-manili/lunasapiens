package com.lunasapiens;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(1) // Ordine di esecuzione del filtro, se necessario
public class RequestLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 4; // Limite massimo di richieste per IP
    private Map<String, Integer> requestCounts = new HashMap<>();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ipAddress = request.getRemoteAddr();

        // Controllo per /subscribe endpoint
        if (request.getRequestURI().equals("/subscribe") && request.getMethod().equals("POST")) {
            handleRequest(request, response, ipAddress);
        }

        // Controllo per /saluti endpoint
        if (request.getRequestURI().equals("/saluti") && request.getMethod().equals("GET")) {
            handleRequest(request, response, ipAddress);
        }

        filterChain.doFilter(request, response);
    }



    private void handleRequest(HttpServletRequest request, HttpServletResponse response, String ipAddress) throws IOException {
        if (!requestCounts.containsKey(ipAddress)) {
            requestCounts.put(ipAddress, 1);
        } else {
            int count = requestCounts.get(ipAddress);
            if (count >= MAX_REQUESTS) {
                // Imposta un flash attribute per indicare al controller di non salvare l'email
                request.setAttribute("skipEmailSave", true);
                response.setStatus(Constants.TOO_MANY_REQUESTS_STATUS_CODE);
                response.getWriter().write("Too many requests from this IP");
                response.getWriter().flush();
                return; // Termina il filtro senza tentare il redirect
            }
            requestCounts.put(ipAddress, count + 1);
        }
    }






}

