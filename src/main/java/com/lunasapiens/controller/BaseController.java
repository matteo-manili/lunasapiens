package com.lunasapiens.controller;

import com.lunasapiens.Constants;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

public class BaseController {


    @ModelAttribute
    public void aggiungiCostanteAlModel(Model model) {
        model.addAttribute("cookieDisabledGoogleAnalytics", Constants.COOKIE_DISABLED_GOOGLE_ANALYTICS);
    }



}
