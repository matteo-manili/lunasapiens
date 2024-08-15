package com.lunasapiens.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // il matchers, indica il path dell'url alla applicazione (esempio localhost:/tema-natale)
        // non della cartella del context dove ci sono i file html

        // "/*" = significa tutti gli url ma non i sotto url (esempio localhost:/contatti)
        // "/*" = significa tutti gli url e anche i sotto url (esempio localhost:/private/privatePage)

        http
        .authorizeHttpRequests(requests -> requests
                .requestMatchers("/*", "/chat-websocket/**", "/js/**" ).permitAll()  // Accesso pubblico alle risorse specificate
                .requestMatchers("/private/**").authenticated()  // Richiede autenticazione per le risorse private
                .anyRequest().denyAll()  // Blocca tutte le altre richieste
        )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")  // Pagina di login personalizzata
                        .permitAll()  // Accesso pubblico alla pagina di login
                )

                //.logout(logout -> logout
                //        .permitAll())  // Permetti il logout per tutti

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/error")  // Pagina di errore per accesso negato
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password("{noop}password")  // {noop} indica che non viene utilizzato l'encoding della password
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

}

