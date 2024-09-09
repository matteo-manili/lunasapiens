package com.lunasapiens.controller;

import com.lunasapiens.Constants;
import com.lunasapiens.Utils;
import com.lunasapiens.service.EmailService;
import com.lunasapiens.service.TelegramBotService;
import com.lunasapiens.config.JwtElements;
import com.lunasapiens.entity.ProfiloUtente;
import com.lunasapiens.repository.ProfiloUtenteRepository;
import com.lunasapiens.service.JwtService;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;
import com.lunasapiens.zodiac.ServizioTemaNatale;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Controller
public class JwtController {

    private static final Logger logger = LoggerFactory.getLogger(JwtController.class);


    @Autowired
    ServizioOroscopoDelGiorno servizioOroscopoDelGiorno;

    @Autowired
    ServizioTemaNatale servizioTemaNatale;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ProfiloUtenteRepository profiloUtenteRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TelegramBotService telegramBotService;









    /**
     * INVIO EMAIL REGISTRAZIOE CON LINK TOKEN
     */
    @PostMapping("/registrazioneUtente")
    public String registrazioneUtente(@RequestParam("email") @Email @NotEmpty String email, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        logger.info("sono in registrazioneUtente");
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
                profiloUtente.setDataUltimoAccesso( LocalDateTime.now() );
                profiloUtenteRepository.save( profiloUtente );
            }
            emailService.inviaemailRegistrazioneUtente(profiloUtente, codeTokenJwt);
            infoMessage = "Utente già iscritto. Ti abbiamo inviato un'email ("+email+") con il link per accedere come utente autenticato.";
        }else {
            try{
                ProfiloUtente newProfiloUtente = new ProfiloUtente( email, null, null, LocalDateTime.now(), null, request.getRemoteAddr(),
                        false, false, UUID.randomUUID().toString() );
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
                headers.add("Location", "/error");
                return ResponseEntity.status(302).headers(headers).build();
            }
        }
    }







    // ############################### CODICE DI TEST JWT ###############################

/*

    @GetMapping("/jwt")
    public String testJWT(Model model) throws NoSuchAlgorithmException {

        // Chiavi Base64 (esempio, sostituisci con le tue chiavi)
        String publicKeyB64 = jwtKeys.getKeyPublic();
        String privateKeyB64 = jwtKeys.getKeyPrivate();

        System.out.println("publicKeyB64 testJWT: " + publicKeyB64);
        System.out.println("privateKeyB64 testJWT: " + privateKeyB64);

        RSAPublicKey rSAPublicKey = decodificaChiaveJwtPublic(publicKeyB64);
        RSAPrivateKey rSAPrivateKey = decodificaChiaveJwtPrivate(privateKeyB64);

        String token = "";

        // Creo un token JWT
        try {
            Algorithm algorithm = Algorithm.RSA256(rSAPublicKey, rSAPrivateKey);
            token = JWT.create()
                    .withIssuer( Constants.JWT_WITH_ISSUER )

                    .withSubject("emailllllll")
                    .withIssuedAt(new Date()) // Aggiunge data di emissione
                    .withExpiresAt(new Date(System.currentTimeMillis() + 86400000)) // Token valido per 1 giorno

                    .sign(algorithm);


            System.out.println("token jwt: "+token);


        } catch (JWTCreationException exc){
            // Invalid Signing configuration / Couldn't convert Claims.
            exc.printStackTrace();
        } catch (Exception exc) {
            exc.printStackTrace();
        }



        // qui verifica se il token è valido
        //token = "TOKENCASUALEeeeeeeeyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCJ9.AbIJTDMFc7yUa5MhvcP03nJPyCPzZtQcGEp-zWfOkEE";

        DecodedJWT decodedJWT;
        try {
            Algorithm algorithm = Algorithm.RSA256(rSAPublicKey, rSAPrivateKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    // specify any specific claim validations
                    .withIssuer( Constants.JWT_WITH_ISSUER )
                    // reusable verifier instance
                    .build();

            decodedJWT = verifier.verify(token);

            // Se arriviamo qui, il token è valido
            System.out.println("Token valido!");
            System.out.println("Issuer: " + decodedJWT.getIssuer());
            System.out.println("Claims: " + decodedJWT.getClaims());


        } catch (JWTVerificationException exception){
            // Invalid signature/claims
            // Token non valido
            System.out.println("Token non valido: " + exception.getMessage());


        }

        return "index";
    }


    public static RSAPublicKey decodificaChiaveJwtPublic(String publicKeyB64){
        // Decodifica Base64
        byte[] publicKeyDecoded = Base64.getDecoder().decode(publicKeyB64);
        // Crea la chiave pubblica da X.509
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyDecoded);
        RSAPublicKey publicKey = null;
        try {
            publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return publicKey;
    }


    public static RSAPrivateKey decodificaChiaveJwtPrivate(String privateKeyB64){

        byte[] privateKeyDecoded = Base64.getDecoder().decode(privateKeyB64);
        // Crea la chiave privata da PKCS#8
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyDecoded);
        RSAPrivateKey rSAPrivateKey = null;
        try {
            rSAPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return rSAPrivateKey;
    }


 */

}

