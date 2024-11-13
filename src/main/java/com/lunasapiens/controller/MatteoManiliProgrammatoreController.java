package com.lunasapiens.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MatteoManiliProgrammatoreController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MatteoManiliProgrammatoreController.class);



    @GetMapping("/matteo-manili-programmatore")
    public String register(Model model, HttpServletRequest request) {

        return "matteo-manili-programmatore";
    }





}

