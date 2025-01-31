package com.lunasapiens.config;

import com.lunasapiens.Constants;
import com.lunasapiens.filter.FilterAuthenticationJwt;

import com.lunasapiens.filter.FilterCSRF;
import com.lunasapiens.filter.FilterCheckUrls;
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
import org.springframework.security.web.csrf.CsrfFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private FilterAuthenticationJwt filterAuthenticationJwt;

    @Autowired
    private FilterCheckUrls filterCheckUrls;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("sono in SecurityConfig securityFilterChain");

        http

            // Configura la protezione CSRF utilizzando un token memorizzato in un cookie accessibile da JavaScript (HttpOnly=false).
            // Il token CSRF viene applicato solo alle richieste HTTP "non-idempotenti" (come POST, PUT, DELETE), che sono le più vulnerabili a questo tipo di attacco.
            // Questo approccio è comune e limita la necessità del token CSRF alle richieste che effettivamente modificano lo stato dell'applicazione,
            // evitando di richiederlo su richieste GET o HEAD che non sono solitamente soggette a CSRF.
            .csrf(csrf -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers("/"+ Constants.DOM_LUNA_SAPIENS_PRIVATE_UPLOAD_IMAGE_ARTICLE)

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
                            "/fragments/**", "/chat-websocket/**", "/css/**", "/js/**",
                            "/"+Constants.DOM_LUNA_SAPIENS_IMAGES_ARTICLE+"/*", "/s3-download/*" ).permitAll()    // Accesso pubblico alle risorse specificate
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


        /*
         * Filtro per aggiungere il token CSRF alle intestazioni della risposta HTTP.
         *
         * Questo filtro viene eseguito dopo il filtro CSRF di Spring (CsrfFilter). Il token CSRF, che è stato precedentemente
         * generato e associato alla richiesta, viene estratto e aggiunto all'intestazione della risposta come "X-CSRF-TOKEN".
         * Questo permette al client (ad esempio, un'applicazione frontend) di accedere al token e includerlo nelle richieste future,
         * garantendo che le richieste siano protette da attacchi di tipo Cross-Site Request Forgery (CSRF).
         *
         * Il filtro è progettato per essere eseguito una sola volta per ogni richiesta, come indicato dalla classe
         * `OncePerRequestFilter`. Dopo aver aggiunto l'intestazione, la richiesta viene passata al filtro successivo nella catena.
         */
        .addFilterAfter(new FilterCSRF(), CsrfFilter.class)
        .addFilterBefore(filterCheckUrls, UsernamePasswordAuthenticationFilter.class)  // filterCheckUrls prima di JWT
        .addFilterBefore(filterAuthenticationJwt, UsernamePasswordAuthenticationFilter.class); // JWT prima dell'autenticazione standard


        return http.build();
    }


}

