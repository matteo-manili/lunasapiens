package com.lunasapiens.config;

import com.lunasapiens.filter.FilterAuthenticationJwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private FilterAuthenticationJwt filterAuthenticationJwt;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        logger.info("sono in SecurityConfig securityFilterChain");

        http
            // Configura la protezione CSRF utilizzando un token memorizzato in un cookie.
            // Il token è accessibile tramite JavaScript (HttpOnly=false) per poterlo includere nelle richieste AJAX.
            // Questo aiuta a prevenire attacchi CSRF, garantendo che ogni richiesta di modifica dello stato (cioè i form di tipo POST, PUT, DELETE)
            // contenga un token valido che viene verificato dal server.
            .csrf(csrf -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )

            //  STATELESS - IF_REQUIRED (se uso STATELESS non funzionano i form perché spring non vede l'autienticazione)
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED )
                    .maximumSessions(1) // Limita il numero di sessioni simultanee di un utente
                    .expiredUrl("/register") // URL da reindirizzare quando la sessione scade
            )

            // il matchers, indica il path dell'url alla applicazione (esempio localhost:/tema-natale)
            // non della cartella del context dove ci sono i file html

            // "/*" = significa tutti gli url ma non i sotto url (esempio localhost:/contatti)
            // "/*" = significa tutti gli url e anche i sotto url (esempio localhost:/private/privatePage)
            .authorizeHttpRequests(requests -> requests
                    .requestMatchers("/", "/*", "/video-oroscopo-giornaliero/*",
                            "/fragments/**", "/chat-websocket/**", "/css/**", "/js/**" ).permitAll()    // Accesso pubblico alle risorse specificate
                    .requestMatchers("/private/**").authenticated()     // Richiede autenticazione per le risorse private
                    .anyRequest().denyAll()     // Blocca tutte le altre richieste
            )

            .formLogin(formLogin -> formLogin
                    .loginPage("/register")  // Pagina di login personalizzata
                    .permitAll()  // Accesso pubblico alla pagina di login
            )

            .exceptionHandling(exceptionHandling -> exceptionHandling
                    .accessDeniedPage("/error")  // Pagina di errore per accesso negato
            )


            // .headers(headers -> headers): Configura le intestazioni di sicurezza HTTP dell'applicazione per proteggerla da attacchi.
            // .frameOptions(frameOptions -> frameOptions): Imposta l'intestazione X-Frame-Options per controllare la visualizzazione delle pagine in iframe.
            // .sameOrigin(): Imposta X-Frame-Options su SAMEORIGIN, permettendo la visualizzazione solo da frame dello stesso dominio per prevenire il clickjacking.
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin()) // Permette solo il rendering dello stesso dominio
            )


        /**
         * Controllo della sequenza di esecuzione: La posizione di un filtro nella catena di filtri è importante perché determina l'ordine in cui i
         * filtri vengono eseguiti. Alcuni filtri, come UsernamePasswordAuthenticationFilter, sono responsabili dell'autenticazione dell'utente tramite
         * nome utente e password. Se hai un filtro che gestisce l'autenticazione tramite JWT (JSON Web Token), dovresti assicurarti che venga eseguito
         * prima del filtro che gestisce la logica di autenticazione tradizionale, altrimenti i token JWT potrebbero non essere validati correttamente.
         */
        .addFilterBefore(filterAuthenticationJwt, UsernamePasswordAuthenticationFilter.class); // Aggiunge il filtro JWT
        return http.build();
    }

}

