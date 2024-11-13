package com.lunasapiens.controller;

import com.lunasapiens.*;
import com.lunasapiens.config.FacebookConfig;
import com.lunasapiens.dto.*;

import com.lunasapiens.entity.ProfiloUtente;
import com.lunasapiens.filter.RateLimiterUser;
import com.lunasapiens.repository.ProfiloUtenteRepository;
import com.lunasapiens.service.EmailService;
import com.lunasapiens.zodiac.*;
import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import com.restfb.*;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.FacebookType;
import com.restfb.types.Page;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class IndexController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    ServizioOroscopoDelGiorno servizioOroscopoDelGiorno;

    @Autowired
    ServizioTemaNatale servizioTemaNatale;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FacebookConfig facebookConfig;

    @Autowired
    private ProfiloUtenteRepository profiloUtenteRepository;


    @GetMapping("/")
    public String rootBase() {
        logger.info("sono in rootBase");
        return "index";
    }





    @GetMapping("/index")
    public RedirectView index() {
        logger.info("sono in index");
        RedirectView redirectView = new RedirectView("/", true);
        return redirectView;
    }


    @GetMapping("/register")
    public String register(Model model, HttpServletRequest request) {
        // dal CeckFilterJwtAutenticator faccio un request.getSession().setAttribute e il redirect a /register.
        // è per questo che qui raccolgo l'eventuale attributo
        String infoError = (String) request.getSession().getAttribute(Constants.INFO_ERROR);
        String infoAlert = (String) request.getSession().getAttribute(Constants.INFO_ALERT);
        String infoMessage = (String) request.getSession().getAttribute(Constants.INFO_MESSAGE);


        model.addAttribute("JWT_EXPIRED_TOKEN_DAY_OF_YEAR", Constants.JWT_EXPIRED_TOKEN_DAY_OF_YEAR);
        model.addAttribute("MAX_MESSAGES_PER_DAY_UTENTE", RateLimiterUser.MAX_MESSAGES_PER_DAY_UTENTE);
        model.addAttribute("MAX_MESSAGES_PER_DAY_ANONYMOUS", RateLimiterUser.MAX_MESSAGES_PER_DAY_ANONYMOUS);
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


    @GetMapping("/private/privatePage")
    public String privatePage(HttpServletRequest request, Principal principal, Model model) {
        logger.info( "sono in: private/privatePage" );
        // constrollo se è presente il cookie per disabilitare Google Analytics
        String disabledAnalyticsCookie = "infoDisabledAnalyticsCookie";
        if( Utils.isPresentCookieDisabledGoogleAnalytics(request) ) {
            model.addAttribute(disabledAnalyticsCookie, "Il cookie 'disable_google_analytics' è true. NON STA tracciando questo dispositivo.");
        }else {
            model.addAttribute(disabledAnalyticsCookie, "Il cookie 'disable_google_analytics' è false. STA tracciando questo dispositivo.");
        }
        return "private/privatePage";
    }

    /**
     * serve a creare un coockie per al dispositivo dell'utente (solo il mio) che indica che google analytics non deve tracciare.
     * lo faccio per mantenere i dati di analytics puliti dai miei test
     */
    @GetMapping("/set-disable-analytics-cookie")
    public ResponseEntity<String> setDisabledAnalyticsCookie(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        Cookie cookie = new Cookie(Constants.COOKIE_DISABLED_GOOGLE_ANALYTICS, "true");
        cookie.setMaxAge(30 * 24 * 60 * 60); // Durata di 30 giorni
        cookie.setPath("/");
        response.addCookie(cookie);
        logger.info("creato cookie di disabilitazione analytics");

        HttpHeaders headers = new HttpHeaders();
        redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Cookie di disabilitazione analytics impostato");
        headers.add("Location", "/private/privatePage");
        return ResponseEntity.status(302).headers(headers).build();
    }

    @GetMapping("/private/cancellaUtente")
    public String cancellaUtente(Principal principal, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        logger.info( "sono in: private/cancellaUtente" );
        if (principal != null) {
            String username = principal.getName();
            logger.info("Username: " + username);
            Optional<ProfiloUtente> optionalProfiloUtente = profiloUtenteRepository.findByEmail(principal.getName());
            if (optionalProfiloUtente.isPresent()) {
                try {
                    // Recupera l'utente e cancella
                    ProfiloUtente profiloUtente = optionalProfiloUtente.get();
                    String email = profiloUtente.getEmail();
                    profiloUtenteRepository.delete(profiloUtente);
                    Utils.clearJwtCookie_ClearSecurityContext(request, response);
                    redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Utente con email " + email +
                            " è stato cancellato con successo. Puoi iscriverti nuovamente in qualsiasi momento.");
                    return "redirect:/register";

                } catch (Exception e) {
                    logger.error("Errore durante la cancellazione dell'utente: " + e.getMessage(), e);
                    redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Errore durante la cancellazione dell'utente.");
                }
            } else {
                logger.error("Utente con email " + username + " non trovato.");
                redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Utente con email " + username + " non trovato.");
            }
        } else {
            logger.error("Utente non trovato.");
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Utente non trovato.");
        }
        Utils.clearJwtCookie_ClearSecurityContext(request, response);
        return "redirect:/error";
    }

    @GetMapping("/logout")
    public RedirectView logout(HttpServletRequest request, HttpServletResponse response, RedirectAttributes attributes) {
        logger.info( "sono in logout" );
        Utils.clearJwtCookie_ClearSecurityContext(request, response);
        attributes.addFlashAttribute(Constants.INFO_MESSAGE, "Logout eseguito. Invia una nuova email per autenticarti.");
        RedirectView redirectView = new RedirectView("/register", true);
        return redirectView;
    }


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
    public String contattiSubmit(@Valid ContactFormDTO contactForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        logger.info("sono in contattiSubmit");
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Errore invio messaggio!");
            return "redirect:/error";
        }
        emailService.inviaEmailContatti(contactForm);
        redirectAttributes.addFlashAttribute(Constants.INFO_MESSAGE, "Messaggio inviato con successo!");
        return "redirect:/contatti";
    }


    @GetMapping("/info-privacy")
    public String infoPrivacy(Model model) {
        return "info-privacy";
    }

    @GetMapping("/termini-di-servizio")
    public RedirectView terminiDiServizio_redirect_301() {
        RedirectView redirectView = new RedirectView("/info-privacy", true);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY); // Imposta il codice 301
        return redirectView;
    }

    @GetMapping("/error")
    public String pageError(HttpServletRequest request, Model model) {
        return "error";
    }

    /**
     * lo uso solo per test
     * Reindirizza alla home page e segnala che la risorsa non esiste più
     */
    @GetMapping("/genera-video")
    public RedirectView gerneraVideo() {
        //scheduledTasks.test_Oroscopo_Segni_Transiti_Aspetti();
        servizioOroscopoDelGiorno.creaOroscopoGiornaliero();
        // Restituisci una RedirectView per reindirizzare alla home page
        RedirectView redirectView = new RedirectView("/", true);
        redirectView.setStatusCode(HttpStatus.GONE); // Imposta il codice di stato 410
        return redirectView;
    }


    /**
     * restituisce il codice html del frammento "header menu", il quale ritorna dalla funziona javascript
     * document.getElementById("header-placeholder").innerHTML = html;
     * E' necessario quando il browser memorizza in cache alcune pagine (soprattuto la pagina root / ) e non visualizza il menu agguiornato.
     */
    @GetMapping("/header")
    @ResponseStatus(HttpStatus.NOT_FOUND) // Impostiamo un codice 404
    public String header() { return "fragments/templateBase :: header"; }





    @GetMapping("/robots.txt")
    public void getRobotsTxt(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        writer.println("User-agent: *");
        for(String url : Constants.URL_NO_INDEX_STATUS_410_LIST){
            writer.println("Disallow: "+url);
        }
        writer.println();
        writer.println("Allow: /download-pdf");
        writer.println();
        writer.println("Sitemap: "+Constants.DOM_LUNA_SAPIENS+"/"+"sitemap.xml");
        writer.close();
    }

    @GetMapping("/sitemap.xml")
    public void getSitemap(HttpServletResponse response) throws IOException {
        // Crea il generatore di sitemap
        WebSitemapGenerator sitemapGenerator = WebSitemapGenerator.builder(Constants.DOM_LUNA_SAPIENS, new File(".")).build();
        // Aggiungi URL alla sitemap
        for (String url : Constants.URL_INDEX_LIST) {
            if (url.equals("/oroscopo")) {
                // Aggiungi la pagina oroscopo con lastmod, changefreq e priority
                WebSitemapUrl oroscopoUrl = new WebSitemapUrl.Options(Constants.DOM_LUNA_SAPIENS + url)
                        .lastMod( Utils.OggiOre0() ) // Data di ultima modifica
                        .changeFreq(ChangeFreq.DAILY) // Frequenza di aggiornamento
                        .priority(1.0)                // Priorità alta
                        .build();
                sitemapGenerator.addUrl(oroscopoUrl);
            } else {
                // Per tutte le altre pagine, aggiungi normalmente
                WebSitemapUrl sitemapUrl = new WebSitemapUrl.Options(Constants.DOM_LUNA_SAPIENS + url).build();
                sitemapGenerator.addUrl(sitemapUrl);
            }
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

    // #######################################################################################
    // ############################# TEST ####################################################
    // #######################################################################################

    @GetMapping("/test")
    public String testFacebook(Model model) {
        try {
            String pageID = facebookConfig.getPageId();
            String appId = facebookConfig.getAppId();
            String appSecret = facebookConfig.getAppSecret();
            // Ottieni il token di accesso dell'app
            AccessToken appAccessToken = new DefaultFacebookClient(Version.LATEST)
                    .obtainAppAccessToken(appId, appSecret);
            // Crea un client Facebook con il token dell'app
            FacebookClient facebookClient = new DefaultFacebookClient(appAccessToken.getAccessToken(), Version.LATEST);
            logger.info("appAccessToken.getAccessToken(): "+appAccessToken. getAccessToken());
            // Ottieni la pagina
            Page page = facebookClient.fetchObject(pageID, Page.class, Parameter.with("fields", "access_token"));
            // Ottieni il token di accesso della pagina
            String pageAccessToken = page.getAccessToken();
            // Crea un client Facebook per la pagina con il token della pagina
            FacebookClient pageClient = new DefaultFacebookClient(pageAccessToken, Version.LATEST);
            // Pubblica un messaggio sulla bacheca della pagina
            pageClient.publish(pageID + "/feed", FacebookType.class, Parameter.with("message", "1: Hello, facebook World!"));
            return "index";
        } catch (FacebookOAuthException e) {
            logger.error("FacebookOAuthException occurred: ", e);
            return "error";
        } catch (Exception e) {
            logger.error("Exception occurred: ", e);
            return "error";
        }
    }



}

