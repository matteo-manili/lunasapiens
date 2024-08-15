package com.lunasapiens.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lunasapiens.Constants;
import com.lunasapiens.EmailService;
import com.lunasapiens.config.FacebookConfig;
import com.lunasapiens.config.JwtConfig;
import com.lunasapiens.dto.ContactFormDTO;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;
import com.lunasapiens.zodiac.ServizioTemaNatale;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


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
    private FacebookConfig facebookConfig;

    @Autowired
    private JwtConfig getJwtRsaKeys;



    @GetMapping("/register")
    public String register(Model model, @ModelAttribute(Constants.INFO_MESSAGE) String infoMessage) {

        //model.addAttribute("contactForm", new ContactFormDTO());
        return "register";
    }




    @PostMapping("/registrazioneSubmit")
    public String registrazioneSubmit(RedirectAttributes redirectAttributes) {
        logger.info("sono in registrazioneSubmit");


        redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Messaggio inviato con successo!");
        return "redirect:/register";
    }







    @GetMapping("/jwt")
    public String testJWT(Model model) throws NoSuchAlgorithmException {

        // Chiavi Base64 (esempio, sostituisci con le tue chiavi)
        String publicKeyB64 = getJwtRsaKeys.getKeyPublic();
        String privateKeyB64 = getJwtRsaKeys.getKeyPrivate();

        System.out.println("publicKeyB64: " + publicKeyB64);
        System.out.println("privateKeyB64: " + privateKeyB64);

        RSAPublicKey rSAPublicKey = decodificaChiaveJwtPublic(publicKeyB64);
        RSAPrivateKey rSAPrivateKey = decodificaChiaveJwtPrivate(privateKeyB64);

        String token = "";

        // Creo un token JWT
        try {
            Algorithm algorithm = Algorithm.RSA256(rSAPublicKey, rSAPrivateKey);
            token = JWT.create()
                    .withIssuer("auth0")
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
                    .withIssuer("auth0")
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




}

