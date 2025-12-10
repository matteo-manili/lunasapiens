package com.lunasapiens.controller;

import com.lunasapiens.Constants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

public class BaseController {


    @ModelAttribute
    public void versionCssStyle(Model model) {
        model.addAttribute("version", "0.2"); // cambia a ogni deploy
    }


    @ModelAttribute
    public void aggiungiCostanteAlModel(Model model) {
        model.addAttribute("cookieDisabledGoogleAnalytics", Constants.COOKIE_DISABLED_GOOGLE_ANALYTICS);
    }


    protected boolean isMatteoManilIdUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false; // Nessun utente autenticato
        }
        String email = authentication.getName(); // Presupponendo che l'email sia l'username
        return Constants.MATTEO_MANILI_GMAIL.equals(email);
    }

    /**
     * con questo metodo se la string
     * ..blog/page=-1 il page viene convertito in ..blog/page=1
     * oppure ..blog/page=XXX viene convertito in ..blog/page=0
     */
    protected int parsePositivePage(String pageParam) {
        try {
            int page = Integer.parseInt(pageParam);
            return Math.abs(page); // Converte i numeri negativi in positivi
        } catch (NumberFormatException e) {
            return 0; // Se non è un numero valido, torna 0
        }
    }

}
