package com.lunasapiens.controller;

import com.lunasapiens.*;
import com.lunasapiens.config.FacebookConfig;
import com.lunasapiens.dto.*;

import com.lunasapiens.zodiac.*;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import com.restfb.*;
import com.restfb.types.FacebookType;
import com.restfb.types.GraphResponse;
import com.restfb.types.Page;
import com.restfb.types.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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

    /**
     * lo uso solo per test
     */
    @GetMapping("/test")
    public String inviaEmail(Model model) {


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




    /**
     * lo uso solo per test
     */
    @GetMapping("/genera-video")
    public String gerneraVideo(Model model) {
        //scheduledTasks.test_Oroscopo_Segni_Transiti_Aspetti();
        scheduledTasks.creaOroscopoGiornaliero();
        return "index";
    }

    @GetMapping("/")
    public String index(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute(Constants.INFO_MESSAGE, "Welcome to our dynamic landing page!");
        return "index";
    }



    @GetMapping("/robots.txt")
    public void getRobotsTxt(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        writer.println("User-agent: *");
        for(String url : Constants.URL_NO_INDEX_LIST){
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

}
