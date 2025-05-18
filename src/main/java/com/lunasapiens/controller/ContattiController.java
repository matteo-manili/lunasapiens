package com.lunasapiens.controller;

import com.lunasapiens.Constants;
import com.lunasapiens.dto.ContactFormDTO;
import com.lunasapiens.service.EmailService;
import com.lunasapiens.service.RecaptchaVerificationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;


@Controller
public class ContattiController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ContattiController.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private RecaptchaVerificationService recaptchaVerificationService;



    @GetMapping("/contatti")
    public String contatti(Model model, Principal principal) {
        if (!model.containsAttribute("contactForm")) {
            ContactFormDTO contactFormDTO = new ContactFormDTO();
            if (principal != null) {
                contactFormDTO.setEmail(principal.getName());
            }
            model.addAttribute("contactForm", contactFormDTO);
        }
        return "contatti";
    }



    @PostMapping("/contattiSubmit")
    public String contattiSubmit(@Valid @ModelAttribute("contactForm") ContactFormDTO contactForm,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 @RequestParam("g-recaptcha-response") String recaptchaResponse) {
        logger.info("sono in contattiSubmit");

        // 1. Verifica se ci sono errori di validazione
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Errore: campi obbligatori mancanti!");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.contactForm", bindingResult);
            redirectAttributes.addFlashAttribute("contactForm", contactForm);
            return "redirect:/contatti";
        }

        // 2. Verifica il token reCAPTCHA
        if (!recaptchaVerificationService.verifyRecaptcha(recaptchaResponse)) {
            logger.warn("verifica reCAPTCHA non valida");
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Errore: verifica reCAPTCHA non valida!");
            return "redirect:/contatti";
        }

        // 3. Se tutto è valido, invia l'email
        emailService.inviaEmailContatti(contactForm);
        redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Messaggio inviato con successo!");
        return "redirect:/contatti";
    }



}
