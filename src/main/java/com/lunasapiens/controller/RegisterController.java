package com.lunasapiens.controller;

import com.lunasapiens.Constants;
import com.lunasapiens.utils.Utils;
import com.lunasapiens.service.EmailService;
import com.lunasapiens.service.RecaptchaEnterpriseService;
import com.lunasapiens.service.TelegramBotService;
import com.lunasapiens.config.JwtElements;
import com.lunasapiens.entity.ProfiloUtente;
import com.lunasapiens.repository.ProfiloUtenteRepository;
import com.lunasapiens.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;


@Controller
public class RegisterController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private ProfiloUtenteRepository profiloUtenteRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TelegramBotService telegramBotService;

    @Autowired
    private RecaptchaEnterpriseService recaptchaEnterpriseService;



    @GetMapping("/register")
    public String register(Model model, HttpServletRequest request) {
        // dal CeckFilterJwtAutenticator faccio un request.getSession().setAttribute e il redirect a /register.
        // è per questo che qui raccolgo l'eventuale attributo
        String infoError = (String) request.getSession().getAttribute(Constants.INFO_ERROR);
        String infoAlert = (String) request.getSession().getAttribute(Constants.INFO_ALERT);
        String infoMessage = (String) request.getSession().getAttribute(Constants.INFO_MESSAGE);

        model.addAttribute("JWT_EXPIRED_TOKEN_DAY_OF_YEAR", Constants.JWT_EXPIRED_TOKEN_DAY_OF_YEAR);
        model.addAttribute("MAX_MESSAGES_PER_DAY_UTENTE", Constants.MAX_MESSAGES_PER_DAY_UTENTE);
        model.addAttribute("MAX_MESSAGES_PER_DAY_ANONYMOUS", Constants.MAX_MESSAGES_PER_DAY_ANONYMOUS);
        if (infoError != null) {
            model.addAttribute(Constants.INFO_ERROR, infoError);
            request.getSession().removeAttribute(Constants.INFO_ERROR); // Rimuovi dalla sessione altrimenti si vede sempore nell pagina
        }
        if (infoAlert != null) {
            model.addAttribute(Constants.INFO_ALERT, infoAlert);
            request.getSession().removeAttribute(Constants.INFO_ALERT); // Rimuovi dalla sessione altrimenti si vede sempore nell pagina
        }
        if (infoMessage != null) {
            model.addAttribute(Constants.INFO_MESSAGE, infoMessage);
            request.getSession().removeAttribute(Constants.INFO_MESSAGE); // Rimuovi dalla sessione altrimenti si vede sempore nell pagina
        }
        return "register";
    }


    /**
     * INVIO EMAIL REGISTRAZIOE CON LINK TOKEN
     */
    @PostMapping("/registrazioneUtente")
    public String registrazioneUtente(@RequestParam("email") @Email @NotEmpty String email, @RequestParam("g-recaptcha-response") String recaptchaResponse,
                                      @RequestParam(value = "hp_name", required = false) String hpName, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        logger.info("sono in registrazioneUtente");


        // 1. Controllo honeypot. HONEYPOT: campo nascosto per bloccare bot automatici. Se compilato, l'invio è sospetto e viene ignorato.
        if (hpName != null && !hpName.isEmpty()) {
            logger.warn("Honeypot compilato, possibile bot: " + email);
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Errore: invio sospetto bloccato.");
            return "redirect:/register";
        }

        // 2. Verifica reCAPTCHA
        if (!recaptchaEnterpriseService.verify(recaptchaResponse)) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Errore: verifica reCAPTCHA non valida!");
            return "redirect:/register";
        }


        // 2. Continua con la logica già presente
        Boolean skipEmailSave = (Boolean) request.getAttribute(Constants.SKIP_EMAIL_SAVE);
        if (skipEmailSave != null && skipEmailSave) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Troppe richieste. Iscrizione negata.");
        }
        Optional<ProfiloUtente> profiloUteteOpt = profiloUtenteRepository.findByEmail( email ); //.orElse(null);
        JwtElements.JwtToken jwtConfigToken = jwtService.generateToken(email);
        String codeTokenJwt = jwtConfigToken.getToken();
        String infoMessage = "";
        if(profiloUteteOpt.isPresent() ) {
            ProfiloUtente profiloUtente = profiloUteteOpt.get();
            if(profiloUtente.getDataCreazione() != null){
                profiloUtente.setDataUltimoAccesso( Utils.getNowRomeEurope().toLocalDateTime() );
                profiloUtenteRepository.save( profiloUtente );
            }
            emailService.inviaemailRegistrazioneUtente(profiloUtente, codeTokenJwt);
            infoMessage = "Utente già iscritto. Ti abbiamo inviato un'email ("+email+") con il link per accedere come utente autenticato.";
        }else {
            try{
                ProfiloUtente newProfiloUtente = new ProfiloUtente( email, null, null, Utils.getNowRomeEurope().toLocalDateTime(), null, request.getRemoteAddr(),
                        false, false, UUID.randomUUID().toString() );
                profiloUtenteRepository.updateSequence();
                newProfiloUtente = profiloUtenteRepository.save( newProfiloUtente );
                emailService.inviaemailRegistrazioneUtente(newProfiloUtente, codeTokenJwt);
                infoMessage = "Ti abbiamo inviato un'email all'indirizzo "+email+" con il link per accedere come utente autenticato.";
                telegramBotService.inviaMessaggio( "Profilo iscritto: "+newProfiloUtente.getEmail());

            } catch (DataIntegrityViolationException e) {
                System.out.println("DataIntegrityViolationException: " + e.getMessage());
                e.printStackTrace();

            }catch (Exception exc){
                System.out.println("Exception: " + exc.getMessage());
                exc.printStackTrace();;
            }
        }
        redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, infoMessage);
        return "redirect:/register";
    }



    /**
     * CONFERMA EMAIL CODE REGISTRAZIOE e SETTA IL COOKIE COL JSON DENTRO
     */
    @GetMapping("/confirmRegistrazioneUtente")
    public ResponseEntity<String> confirmRegistrazione(@RequestParam(name = "code", required = true) String codeTokenJwt,
                                                       RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        logger.info("confirmRegistrazione");
        JwtElements.JwtDetails jwtDetails = jwtService.validateToken( codeTokenJwt );
        HttpHeaders headers = new HttpHeaders(); String pageRegister = "/register";
        if( jwtDetails.isSuccess() ) {
            Optional<ProfiloUtente> profiloUtenteOpt = profiloUtenteRepository.findByEmail( jwtDetails.getSubject() );
            if( profiloUtenteOpt.isPresent() ){
                // Creazione del cookie con il token JWT
                Utils.creaCookieTokenJwt(response, jwtDetails);
                redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Autenticazione completata con successo. Hai effettuato l'accesso " +
                        "con l'email: " + jwtDetails.getSubject());
                headers.add("Location", "/private/privatePage");
                return ResponseEntity.status(302).headers(headers).build();

            }else{
                Utils.clearJwtCookie_ClearSecurityContext(request, response);
                redirectAttributes.addFlashAttribute(Constants.INFO_ALERT, "Email non riconosciuta nel sistema. Iscriviti di nuovo.");
                headers.add("Location", pageRegister);
                return ResponseEntity.status(302).headers(headers).build();
            }

        }else {
            Utils.clearJwtCookie_ClearSecurityContext(request, response);
            if (jwtDetails.isTokenScaduto()) {
                logger.info("token scaduto");
                redirectAttributes.addFlashAttribute(Constants.INFO_ALERT, Constants.MESSAGE_AUTENTICAZIONE_SCADUTA_INVIA_NUOVA_EMAIL);
                headers.add("Location", pageRegister);
                return ResponseEntity.status(302).headers(headers).build();

            } else {
                redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, jwtDetails.getMessaggioErroreJwt());
                headers.add("Location", "/register");
                return ResponseEntity.status(302).headers(headers).build();
            }
        }
    }




}

