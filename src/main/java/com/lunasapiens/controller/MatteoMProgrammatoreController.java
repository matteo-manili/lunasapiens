package com.lunasapiens.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class MatteoMProgrammatoreController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MatteoMProgrammatoreController.class);


    private static final String URL_PAGE_MATTEO_M_PROG = "matteo-m-programmatore";



    @GetMapping("/"+URL_PAGE_MATTEO_M_PROG)
    public String matteo_m_programmatore(Model model, HttpServletRequest request) {
        return URL_PAGE_MATTEO_M_PROG;
    }


    @GetMapping("/matteo-manili-programmatore")
    public RedirectView matteo_manili_prog_redirect_301() {
        RedirectView redirectView = new RedirectView("/"+URL_PAGE_MATTEO_M_PROG, true);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY); // Imposta il codice 301
        return redirectView;
    }


    @GetMapping("/curriculum")
    public RedirectView curriculum_redirect_301() {
        RedirectView redirectView = new RedirectView("/"+URL_PAGE_MATTEO_M_PROG, true);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY); // Imposta il codice 301
        return redirectView;
    }






}

