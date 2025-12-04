package com.lunasapiens.controller;

import com.lunasapiens.Constants;
import com.lunasapiens.utils.Utils;
import com.lunasapiens.entity.ProfiloUtente;
import com.lunasapiens.repository.ProfiloUtenteRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.Optional;

@Controller
public class PrivateUtenteController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(PrivateUtenteController.class);

    @Autowired
    private ProfiloUtenteRepository profiloUtenteRepository;


    @GetMapping("/private/privatePage")
    public String privatePage(Principal principal, HttpServletRequest request, Model model) {
        logger.info( "sono in: private/privatePage" );

        ProfiloUtente profiloUtente = profiloUtenteRepository.findByEmail(principal.getName()).orElse(null);
        model.addAttribute("isSubscribedOroscGiorn", profiloUtente.isEmailOroscopoGiornaliero());

        // constrollo se è presente il cookie per disabilitare Google Analytics
        String disabledAnalyticsCookie = "infoDisabledAnalyticsCookie";
        if( Utils.isPresentCookieDisabledGoogleAnalytics(request) ) {
            model.addAttribute(disabledAnalyticsCookie, "Il cookie 'disable_google_analytics' è true. NON STA tracciando questo dispositivo.");
        }else {
            model.addAttribute(disabledAnalyticsCookie, "Il cookie 'disable_google_analytics' è false. STA tracciando questo dispositivo.");
        }

        return "private/privatePage";
    }


    @PostMapping("/private/subscribe-oroscopo")
    public RedirectView subscribeOroscopo(Principal principal, @RequestParam(value = "subscribedOroscGiorn", required = false) String oroscopo,
                                    RedirectAttributes redirectAttributes) {
        logger.info( "sono in private/subscribe-oroscopo" );
        if (principal != null) {
            ProfiloUtente profiloUtente = profiloUtenteRepository.findByEmail( principal.getName() ).orElse(null);
            if (oroscopo != null) {
                if(profiloUtente != null) {
                    profiloUtente.setEmailOroscopoGiornaliero(true);
                    profiloUtenteRepository.save(profiloUtente);
                    redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Ti sei iscritto all'Oroscopo del Giorno.");
                }
            } else {
                if(profiloUtente != null) {
                    profiloUtente.setEmailOroscopoGiornaliero(false);
                    profiloUtenteRepository.save(profiloUtente);
                    redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Hai annullato l'iscrizione all'Oroscopo del Giorno.");
                }
            }
        } else {
            logger.error("Utente non trovato.");
            return new RedirectView("/error", true);
        }

        return new RedirectView("/private/privatePage", true);
    }

    @GetMapping("/private/logout")
    public RedirectView logout(Principal principal, HttpServletRequest request, HttpServletResponse response, RedirectAttributes attributes) {
        logger.info( "sono in private/logout" );
        if (principal != null) {
            Utils.clearJwtCookie_ClearSecurityContext(request, response);
            attributes.addFlashAttribute(Constants.INFO_MESSAGE, "Logout eseguito. Invia una nuova email per autenticarti.");
            return new RedirectView("/register", true);
        } else {
            logger.error("Utente non trovato.");
            return new RedirectView("/error", true);
        }
    }


    @GetMapping("/private/cancellaUtente")
    public String cancellaUtente(Principal principal, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        logger.info( "sono in private/cancellaUtente" );
        if (principal != null) {
            String username = principal.getName();
            logger.info("Username: " + username);
            Optional<ProfiloUtente> optionalProfiloUtente = profiloUtenteRepository.findByEmail(principal.getName());
            if (optionalProfiloUtente.isPresent()) {
                try {
                    // Recupera l'utente e cancella
                    ProfiloUtente profiloUtente = optionalProfiloUtente.get();
                    String email = profiloUtente.getEmail();
                    profiloUtenteRepository.delete(profiloUtente);
                    Utils.clearJwtCookie_ClearSecurityContext(request, response);
                    redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Utente con email " + email +
                            " è stato cancellato con successo. Puoi iscriverti nuovamente in qualsiasi momento.");
                    return "redirect:/register";

                } catch (Exception e) {
                    logger.error("Errore durante la cancellazione dell'utente: " + e.getMessage(), e);
                    redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Errore durante la cancellazione dell'utente.");
                }
            } else {
                logger.error("Utente con email " + username + " non trovato.");
                redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Utente con email " + username + " non trovato.");
            }
        } else {
            logger.error("Utente non trovato.");
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Utente non trovato.");
        }
        Utils.clearJwtCookie_ClearSecurityContext(request, response);
        return "redirect:/error";
    }




    /**
     * serve a creare un coockie per al dispositivo dell'utente (solo il mio) che indica che google analytics non deve tracciare.
     * lo faccio per mantenere i dati di analytics puliti dai miei test
     */
    @GetMapping("/set-disable-analytics-cookie")
    public ResponseEntity<String> setDisabledAnalyticsCookie(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        Cookie cookie = new Cookie(Constants.COOKIE_DISABLED_GOOGLE_ANALYTICS, "true");
        cookie.setMaxAge(30 * 24 * 60 * 60); // Durata di 30 giorni
        cookie.setPath("/");
        response.addCookie(cookie);
        logger.info("creato cookie di disabilitazione analytics");

        HttpHeaders headers = new HttpHeaders();
        redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Cookie di disabilitazione analytics impostato");
        headers.add("Location", "/private/privatePage");
        return ResponseEntity.status(302).headers(headers).build();
    }


}

