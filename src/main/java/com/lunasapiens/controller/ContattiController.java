package com.lunasapiens.controller;

import com.lunasapiens.Constants;
import com.lunasapiens.dto.ContactFormDTO;
import com.lunasapiens.dto.CoordinateDTO;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.service.EmailService;
import com.lunasapiens.service.RecaptchaVerificationService;
import com.lunasapiens.zodiac.BuildInfoAstrologiaAstroSeek;
import com.lunasapiens.zodiac.ServizioSinastria;
import com.theokanning.openai.completion.chat.ChatMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Controller
public class ContattiController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ContattiController.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private RecaptchaVerificationService recaptchaVerificationService;



    @GetMapping("/contatti")
    public String contatti(Model model, Principal principal) {
        if (principal != null) {
            ContactFormDTO contactFormDTO = new ContactFormDTO();
            contactFormDTO.setEmail( principal.getName() );
            model.addAttribute("contactForm", contactFormDTO);
        }else{
            model.addAttribute("contactForm", new ContactFormDTO());
        }
        return "contatti";
    }




    @PostMapping("/contattiSubmit")
    public String contattiSubmit(@Valid ContactFormDTO contactForm, BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                 @RequestParam("g-recaptcha-response") String recaptchaResponse) {
        logger.info("sono in contattiSubmit");

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Errore: campi obbligatori mancanti!");
        }



        // 1. Verifica il token reCAPTCHA
        if (!recaptchaVerificationService.verifyRecaptcha(recaptchaResponse)) {
            logger.warn("verifica reCAPTCHA non valida");
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Errore: verifica reCAPTCHA non valida!");
            return "redirect:/contatti";
        }

        // 2. Verifica se ci sono errori di validazione nel form
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Errore invio messaggio!");
            return "redirect:/contatti";
        }

        // 3. Se la verifica è passata, invia l'email
        emailService.inviaEmailContatti(contactForm);
        redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Messaggio inviato con successo!");
        return "redirect:/contatti";
    }









}
