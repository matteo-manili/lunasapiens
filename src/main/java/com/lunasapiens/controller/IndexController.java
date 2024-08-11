package com.lunasapiens.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lunasapiens.*;
import com.lunasapiens.config.FacebookConfig;
import com.lunasapiens.config.JwtConfig;
import com.lunasapiens.dto.*;

import com.lunasapiens.zodiac.*;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import com.restfb.*;
import com.restfb.types.FacebookType;
import com.restfb.types.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;


@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private ScheduledTasks scheduledTasks;

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



    /**
     * lo uso solo per test
     */
    @GetMapping("/test")
    public String testFacebook(Model model) {


        String pageID = facebookConfig.getPageId();
        String appId = facebookConfig.getAppId();
        String appSecrtet = facebookConfig.getAppSecret();

        int counter = 1;

        AccessToken accessToken =
                new DefaultFacebookClient(Version.LATEST).obtainAppAccessToken( appId, appSecrtet);
        logger.info( "accessToken: "+accessToken );

        FacebookClient facebookClient = new DefaultFacebookClient(accessToken.getAccessToken(), Version.LATEST);

        // Ottieni le informazioni dell'utente (assicurati che il token di accesso abbia i permessi necessari)
        //User myuser = facebookClient.fetchObject("me", User.class);

        // Ottieni la pagina che l'utente gestisce
        //Page mypage = facebookClient.fetchObject(pageID, Page.class);


        Page page = facebookClient.fetchObject(pageID, Page.class, Parameter.with("fields", "access_token"));
        String pageAccessToken = page.getAccessToken();


        FacebookClient pageClient = new DefaultFacebookClient(pageAccessToken, Version.LATEST);
        pageClient.publish(pageID + "/feed", FacebookType.class, Parameter.with("message", Integer.toString(counter) + ": Hello, facebook World!"));

        return "index";
    }






    /**
     * pagina contatti
     */
    @GetMapping("/contatti")
    public String contatti(Model model) {
            model.addAttribute("contactForm", new ContactFormDTO());
        return "contatti";
    }


    @PostMapping("/contattiSubmit")
    public String contattiSubmit(@Valid ContactFormDTO contactForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Errore invio messaggio!");
            return "redirect:/error";
        }
        emailService.inviaEmailContatti(contactForm);
        redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Messaggio inviato con successo!");
        return "redirect:/contatti";
    }


    @GetMapping("/error")
    public String pageError(HttpServletRequest request, Model model) {
        return "error";
    }

    @GetMapping("/info-privacy")
    public String infoPrivacy(Model model) { return "info-privacy"; }


    @GetMapping("/termini-di-servizio")
    public RedirectView terminiDiServizio() {
        RedirectView redirectView = new RedirectView("/info-privacy", true);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY); // Imposta il codice 301
        return redirectView;
    }



    /**
     * lo uso solo per test
     * Reindirizza alla home page e segnala che la risorsa non esiste più
     */
    @GetMapping("/genera-video")
    public RedirectView gerneraVideo() {

        //scheduledTasks.test_Oroscopo_Segni_Transiti_Aspetti();
        scheduledTasks.creaOroscopoGiornaliero();

        // Restituisci una RedirectView per reindirizzare alla home page
        RedirectView redirectView = new RedirectView("/index", true);
        redirectView.setStatusCode(HttpStatus.GONE); // Imposta il codice di stato 410
        return redirectView;
    }



    @GetMapping("/")
    public String index() {
        return "index";
    }



    @GetMapping("/robots.txt")
    public void getRobotsTxt(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        writer.println("User-agent: *");
        for(String url : Constants.URL_NO_INDEX_STATUS_410_LIST){
            writer.println("Disallow: "+url);
        }
        writer.println();
        writer.println("Sitemap: "+Constants.DOM_LUNA_SAPIENS+"/"+"sitemap.xml");
        writer.close();
    }

    @GetMapping("/sitemap.xml")
    public void getSitemap(HttpServletResponse response) throws IOException {
        // Crea il generatore di sitemap
        WebSitemapGenerator sitemapGenerator = WebSitemapGenerator.builder(Constants.DOM_LUNA_SAPIENS, new File(".")).build();

        // Aggiungi URL alla sitemap
        List<String> urlsForIndex = List.of(
                "/",
                "/oroscopo",
                "/tema-natale",
                "/contatti",
                "/info-privacy"
        );

        for (String url : urlsForIndex) {
            WebSitemapUrl sitemapUrl = new WebSitemapUrl.Options(Constants.DOM_LUNA_SAPIENS + url).build();
            sitemapGenerator.addUrl(sitemapUrl);
        }

        // Genera la sitemap
        List<String> sitemapUrls = sitemapGenerator.writeAsStrings();

        // Imposta il tipo di contenuto e restituisci la sitemap
        response.setContentType("application/xml");
        response.getWriter().write(String.join("\n", sitemapUrls));
    }


    /**
     * 1. Pagina rimossa definitivamente e non verrà sostituita
     * Codice di stato 410 (Gone)
     * Perché: Il codice 410 comunica chiaramente ai motori di ricerca che la pagina è stata rimossa permanentemente e non tornerà. I motori di ricerca
     * tendono a rimuovere più rapidamente dai loro indici le pagine che rispondono con un 410 rispetto a un 404.
     *
     *
     * 2. Pagina non più disponibile, ma non sei sicuro se sarà ripristinata in futuro
     * Codice di stato 404 (Not Found)
     * Perché: Un 404 è il metodo standard per indicare che una pagina non esiste al momento. Lascia aperta la possibilità di reintrodurre la pagina
     * in futuro senza creare confusione per i motori di ricerca.
     *
     *
     * 3. Pagina sostituita da un'altra pagina
     * Reindirizzamento 301 (Permanente)
     * Perché: Un reindirizzamento 301 indica che la pagina è stata spostata in modo permanente a un nuovo URL. Questo aiuta a preservare il ranking
     * nei motori di ricerca e reindirizza i visitatori alla nuova pagina senza interruzioni.
     * - Usare un 301 anche con il Tag Canonico!
     * Consolidamento del traffico SEO: Assicura che tutto il traffico, il link juice, e l'autorità della pagina si concentrino sull'URL corretto.
     * es. <link rel="canonical" href="https://esempio.com/nuova-pagina">
     * - Scenario tipico: Hai una vecchia pagina (A) che è stata sostituita da una nuova pagina (B).
     * Imposta il reindirizzamento 301: Reindirizza la vecchia pagina A verso la nuova pagina B.
     * - Imposta il metatag canonico: Sulla pagina B (la nuova pagina), aggiungi un metatag canonical che punta a se stessa
     * (<link rel="canonical" href="https://esempio.com/nuova-pagina">). Questo conferma ai motori di ricerca che la nuova pagina B è l'URL preferito
     * per il contenuto.
     *
     *
     * 4. Pagina ancora esistente ma non vuoi che venga indicizzata
     * Metatag Robots con "noindex" o X-Robots-Tag
     * Perché: Il metatag noindex o l'header X-Robots-Tag: noindex impedisce ai motori di ricerca di indicizzare una pagina senza rimuoverla
     * completamente dal sito. Questo è utile per pagine come termini di servizio, pagine di login, o altre che non desideri appaiano nei risultati
     * di ricerca.
     *
     *
     * 5. Blocco dell'indicizzazione di una pagina o directory intera
     * File robots.txt
     * Perché: Il robots.txt è utile per bloccare l'accesso di crawler a intere sezioni del sito, ma non garantisce la rimozione di pagine già
     * indicizzate. È più adatto a prevenire l'indicizzazione futura piuttosto che rimuovere pagine esistenti.
     * Se lo si usa con il 410 o 404 è ancora più forte
     *
     *
     * 6. Rimozione immediata di una pagina già indicizzata da Google
     * Rimozione tramite Google Search Console
     * Perché: Questo metodo è rapido ed efficace per rimuovere immediatamente una pagina dagli indici di Google. È utile se hai bisogno di una
     * rimozione urgente o temporanea.
     *
     *
     * Conclusione
     * Per rimozione definitiva: Usa il codice 410.
     * Per una rimozione standard: Usa il codice 404.
     * Per sostituire una pagina: Usa un 301.
     * Per impedire l'indicizzazione senza rimuovere: Usa noindex.
     * Per rimozione immediata da Google: Usa Google Search Console.
     */


}

