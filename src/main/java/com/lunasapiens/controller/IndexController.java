package com.lunasapiens.controller;

import com.lunasapiens.Constants;
import com.lunasapiens.entity.ArticleContent;
import com.lunasapiens.repository.ArticleContentRepository;
import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class IndexController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private ArticleContentRepository articleContentRepository;


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
        writer.println("Sitemap: "+Constants.DOM_LUNA_SAPIENS+"/"+"sitemap.xml");
        writer.close();
    }


    @GetMapping("/sitemap.xml")
    public void getSitemap(HttpServletResponse response) throws IOException, ParseException {
        WebSitemapGenerator sitemapGenerator =
                WebSitemapGenerator.builder(Constants.DOM_LUNA_SAPIENS, new File(".")).build();
        // 1️⃣ Pagine statiche
        List<PageInfo> staticPages = Arrays.asList(
                new PageInfo("/", ChangeFreq.MONTHLY, 1.0),
                new PageInfo("/psicologo", ChangeFreq.MONTHLY, 0.9),
                new PageInfo("/blog", ChangeFreq.WEEKLY, 0.9),
                new PageInfo("/register", ChangeFreq.YEARLY, 0.5),
                new PageInfo("/contatti", ChangeFreq.YEARLY, 0.5),
                new PageInfo("/info-privacy", ChangeFreq.YEARLY, 0.5)
        );
        for (PageInfo page : staticPages) {
            sitemapGenerator.addUrl(
                    new WebSitemapUrl.Options(Constants.DOM_LUNA_SAPIENS + page.getPath())
                            .changeFreq(page.getChangeFreq())
                            .priority(page.getPriority())
                            .build()
            );
        }
        // 2️⃣ Articoli del blog
        List<ArticleContent> articles = articleContentRepository.findAllLight();
        for (ArticleContent article : articles) {
            LocalDate lastModLocalDate = article.getCreatedAt().toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String lastModStr = lastModLocalDate.format(formatter);
            sitemapGenerator.addUrl(
                    new WebSitemapUrl.Options(Constants.DOM_LUNA_SAPIENS + "/blog/" + article.getSeoUrl())
                            .lastMod(lastModStr)  // <--- Passare sempre una String altrimenti su heroku fa vedere ore minuti e secondi
                            .changeFreq(ChangeFreq.MONTHLY)
                            .priority(0.8)
                            .build()
            );
        }
        // 3️⃣ Output XML
        response.setContentType("application/xml");
        List<String> sitemapXml = sitemapGenerator.writeAsStrings();
        response.getWriter().write(String.join("\n", sitemapXml));
    }



    // Classe di supporto per pagine statiche
    static class PageInfo {
        private final String path;
        private final ChangeFreq changeFreq;
        private final double priority;

        public PageInfo(String path, ChangeFreq changeFreq, double priority) {
            this.path = path;
            this.changeFreq = changeFreq;
            this.priority = priority;
        }

        public String getPath() { return path; }
        public ChangeFreq getChangeFreq() { return changeFreq; }
        public double getPriority() { return priority; }
    }



/*
    @GetMapping("/sitemap.xml")
    public void getSitemap(HttpServletResponse response) throws IOException {
        // Crea il generatore di sitemap
        WebSitemapGenerator sitemapGenerator = WebSitemapGenerator.builder(Constants.DOM_LUNA_SAPIENS, new File(".")).build();
        // Aggiungi URL alla sitemap
        for (String url : Constants.URL_INDEX_LIST) {
            if (url.equals("/oroscopo")) {
                // Aggiungi la pagina oroscopo con lastmod, changefreq e priority
                Date lastModDate = Utils.toDate((Utils.OggiRomaOre0()));
                WebSitemapUrl oroscopoUrl = new WebSitemapUrl.Options(Constants.DOM_LUNA_SAPIENS + url)
                        .lastMod( lastModDate  ) // Data di ultima modifica
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

 */


    @GetMapping("/matteo-manili-programmatore")
    public RedirectView redirect_matteoManiliProgrammatore() {
        RedirectView redirectView = new RedirectView("/", true);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY); // Imposta il codice 301
        return redirectView;
    }

    @GetMapping("/matteo-m-programmatore")
    public RedirectView redirect_matteoMProgrammatore() {
        RedirectView redirectView = new RedirectView("/", true);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY); // Imposta il codice 301
        return redirectView;
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

