package com.lunasapiens.config;

import com.lunasapiens.filter.FilterRequestResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private FilterRequestResponse filterRequestResponse;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        logger.info("sono in SecurityConfig securityFilterChain");

        http

            //  STATELESS - IF_REQUIRED (se uso STATELESS non funzionano i form perché spring non vede l'autienticazione)
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED )
            )

            // il matchers, indica il path dell'url alla applicazione (esempio localhost:/tema-natale)
            // non della cartella del context dove ci sono i file html

            // "/*" = significa tutti gli url ma non i sotto url (esempio localhost:/contatti)
            // "/*" = significa tutti gli url e anche i sotto url (esempio localhost:/private/privatePage)
            .authorizeHttpRequests(requests -> requests
                    .requestMatchers("/", "/*", "/chat-websocket/**", "/css/**", "/js/**" ).permitAll()  // Accesso pubblico alle risorse specificate
                    .requestMatchers("/private/**").authenticated()  // Richiede autenticazione per le risorse private
                    .anyRequest().denyAll()  // Blocca tutte le altre richieste
            )
            .formLogin(formLogin -> formLogin
                    .loginPage("/register")  // Pagina di login personalizzata
                    .permitAll()  // Accesso pubblico alla pagina di login
            )

            .exceptionHandling(exceptionHandling -> exceptionHandling
                    .accessDeniedPage("/error")  // Pagina di errore per accesso negato
            )

        /**
         * Controllo della sequenza di esecuzione: La posizione di un filtro nella catena di filtri è importante perché determina l'ordine in cui i
         * filtri vengono eseguiti. Alcuni filtri, come UsernamePasswordAuthenticationFilter, sono responsabili dell'autenticazione dell'utente tramite
         * nome utente e password. Se hai un filtro che gestisce l'autenticazione tramite JWT (JSON Web Token), dovresti assicurarti che venga eseguito
         * prima del filtro che gestisce la logica di autenticazione tradizionale, altrimenti i token JWT potrebbero non essere validati correttamente.
         */
        .addFilterBefore(filterRequestResponse, UsernamePasswordAuthenticationFilter.class); // Aggiunge il filtro JWT


        return http.build();
    }




    @Bean
    public UserDetailsService userDetailsService() {
        /**
         * Questo codice crea un oggetto UserDetails per un utente con nome utente "user", password "password" (non codificata), e il ruolo "USER".
         * L'annotazione {noop} indica che non viene applicata alcuna codifica alla password; è usata per scopi di sviluppo o test, ma non è sicura per un ambiente di produzione.
         */
        UserDetails user = User.builder()
                .username("user")
                .password("{noop}password")  // {noop} indica che non viene utilizzato l'encoding della password
                .roles("USER")
                .build();

        /**
         * emplicità: Utilizzando InMemoryUserDetailsManager, la configurazione dell'utente viene mantenuta in memoria, il che è utile
         * per test o semplici applicazioni che non richiedono un database esterno per la gestione degli utenti.
         */
        return new InMemoryUserDetailsManager(user);
    }

}

