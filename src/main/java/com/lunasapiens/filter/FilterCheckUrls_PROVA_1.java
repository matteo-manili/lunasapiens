package com.lunasapiens.filter;

import com.lunasapiens.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// TODO da continuare serve a gestire la ricerca sempatica nel blog, gli utenti che fanno troppe ricerche devono essere bloccati

//@Component
//@Order(2)
public class FilterCheckUrls_PROVA_1 extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(FilterCheckUrls_PROVA_1.class);

    private static final int MAX_REQUESTS_DEFAULT = 10; // per blocchi permanenti
    private static final int MAX_REQUESTS_BLOG_SEARCH = 3; // per ricerca blog
    private static final long BLOG_SEARCH_BLOCK_DURATION_MS = 1 * 60 * 1000; // 1 minuto

    private final ConcurrentMap<String, Integer> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> blockedIps = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ipAddress = request.getRemoteAddr();

        // 🔓 Escludi fragment interni
        if (request.getRequestURI().equals("/header")) {
            filterChain.doFilter(request, response);
            return;
        }


        // Controllo se l'IP è bloccato (temporaneo o permanente)
        Long blockedTime = blockedIps.get(ipAddress);
        if (blockedTime != null) {
            long now = System.currentTimeMillis();
            boolean isTemporary = blockedTime != Long.MAX_VALUE;
            if ((isTemporary && now - blockedTime < BLOG_SEARCH_BLOCK_DURATION_MS) || !isTemporary) {
                logger.warn("IP bloccato: {} per URI: {}", ipAddress, request.getRequestURI());
                response.setStatus(Constants.TOO_MANY_REQUESTS_STATUS_CODE);
                String msg = isTemporary ? "Too many requests. Try again later." : "Access permanently blocked.";
                response.getWriter().write(msg);
                response.getWriter().flush();
                return; // Interrompe la richiesta
            } else {
                // rimuovi blocco scaduto
                blockedIps.remove(ipAddress);
                requestCounts.remove(ipAddress);
            }
        }

        // Endpoint sensibili con blocco permanente
        if ((request.getRequestURI().equals("/registrazioneUtente") && request.getMethod().equals("POST"))
                || (request.getRequestURI().equals("/contattiSubmit") && request.getMethod().equals("POST"))
                || (request.getRequestURI().equals("/" + Constants.DOM_LUNA_SAPIENS_SUBSCRIBE_OROSC_GIORN) && request.getMethod().equals("POST"))
                || (request.getRequestURI().equals("/" + Constants.DOM_LUNA_SAPIENS_CONFIRM_EMAIL_OROSC_GIORN) && request.getMethod().equals("GET"))
                || (request.getRequestURI().equals("/" + Constants.DOM_LUNA_SAPIENS_CANCELLA_ISCRIZ_OROSC_GIORN) && request.getMethod().equals("GET"))
                || (request.getRequestURI().equals("/genera-video") && request.getMethod().equals("GET"))
        ) {
            handleMaxRequestRequest(request, response, ipAddress, false, MAX_REQUESTS_DEFAULT, 0);
            if (response.isCommitted()) {
                return;
            }
        }

        // Form ricerca blog con blocco temporaneo
        if (request.getRequestURI().equals("/" + Constants.DOM_LUNA_SAPIENS_BLOG)
                && request.getParameter("search") != null
                && request.getMethod().equals("GET")) {

            handleMaxRequestRequest(request, response, ipAddress, true, MAX_REQUESTS_BLOG_SEARCH, BLOG_SEARCH_BLOCK_DURATION_MS);
            if (response.isCommitted()) {
                return;
            }
        }

        // URL no index
        for (String urlNoIndex : Constants.URL_NO_INDEX_STATUS_410_LIST) {
            if (request.getRequestURI().equals(urlNoIndex)) {
                response.setStatus(HttpServletResponse.SC_GONE);
                response.setHeader("X-Robots-Tag", "noindex, nofollow");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void handleMaxRequestRequest(HttpServletRequest request, HttpServletResponse response, String ipAddress,
                                         boolean temporaryBlock, int maxRequests, long blockDurationMs) throws IOException {

        // Incremento contatore
        requestCounts.putIfAbsent(ipAddress, 0);
        int count = requestCounts.get(ipAddress) + 1;
        requestCounts.put(ipAddress, count);

        if (count >= maxRequests) {
            if (temporaryBlock) {
                blockedIps.put(ipAddress, System.currentTimeMillis());
                response.setHeader("Retry-After", String.valueOf(blockDurationMs / 1000));
            } else {
                blockedIps.put(ipAddress, Long.MAX_VALUE);
            }
            // Imposta un flash attribute per indicare al controller di non salvare l'email
            request.setAttribute(Constants.SKIP_EMAIL_SAVE, true);
            response.setStatus(Constants.TOO_MANY_REQUESTS_STATUS_CODE);
            return;
        }
    }
}
