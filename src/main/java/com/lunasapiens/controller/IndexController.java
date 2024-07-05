package com.lunasapiens.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunasapiens.*;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.dto.OroscopoGiornalieroDTO;
import com.lunasapiens.entity.EmailUtenti;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.EmailService;
import com.lunasapiens.repository.EmailUtentiRepository;
import com.lunasapiens.service.OroscopoGiornalieroService;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private String getGeonamesUsername;

    @Autowired
    private ScheduledTasks scheduledTasks;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ServizioOroscopoDelGiorno servizioOroscopoDelGiorno;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailUtentiRepository emailUtentiRepository;



    private OroscopoGiornalieroService oroscopoGiornalieroService;
    @Autowired
    public IndexController(OroscopoGiornalieroService oroscopoGiornalieroService) {
        this.oroscopoGiornalieroService = oroscopoGiornalieroService;
    }

    public final String redirectAttributInfoSubscription = "infoSubscription";

    @GetMapping("/")
    public String index(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("message", "Welcome to our dynamic landing page!");
        return "index";
    }





    @GetMapping("/coordinate")
    @ResponseBody
    public List<Map<String, Object>> getCoordinates(@RequestParam String cityName) {

        // style = SHORT,MEDIUM,LONG,FULL. Si ottengono più dettaglio geografioci
        // paramentro di ricerca: name_startsWith = si può scriverew anche parzialmente il nome della localitò . Se si usa il paramentro
        // paramentro di ricerca: q = bisogna scrivere il nome completo

        String url = "http://api.geonames.org/searchJSON?name_startsWith=" + cityName + "&username=" + getGeonamesUsername + "&style=MEDIUM&lang=it&maxRows=5";
        String response = restTemplate.getForObject(url, String.class);

        System.out.println("geonames.org Response JSON: " + response);

        List<Map<String, Object>> locations = new ArrayList<>();

        // visualizzare:
        // name : "Ostia" | adminName1 ("Lazio") | countryName | countryCode | lat | lng

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode geonames = root.path("geonames");
            for (JsonNode node : geonames) {
                Map<String, Object> location = new HashMap<>();
                location.put("name", node.path("name").asText());
                location.put("adminName1", node.path("adminName1").asText());
                location.put("countryName", node.path("countryName").asText());
                location.put("countryCode", node.path("countryCode").asText());
                location.put("lat", node.path("lat").asText());
                location.put("lng", node.path("lng").asText());
                locations.add(location);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return locations;
    }



    @GetMapping("/temaNataleSubmit")
    public String temaNataleSubmit(@RequestParam("datetime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime,
            @RequestParam("cityLat") String cityLat, @RequestParam("cityLng") String cityLng, @RequestParam("cityName") String cityName,
                                   @RequestParam("regioneName") String regioneName, @RequestParam("statoName") String statoName, Model model) {

        // Estrai le singole componenti della data e ora
        int hour = datetime.getHour();
        int minute = datetime.getMinute();
        int day = datetime.getDayOfMonth();
        int month = datetime.getMonthValue();
        int year = datetime.getYear();

        System.out.println("Ora: " + hour);
        System.out.println("Minuti: " + minute);
        System.out.println("Giorno: " + day);
        System.out.println("Mese: " + month);
        System.out.println("Anno: " + year);

        System.out.println("cityName: " + cityName);
        System.out.println("regioneName: " + regioneName);
        System.out.println("statoName: " + statoName);
        System.out.println("Latitude: " + cityLat);
        System.out.println("Longitude: " + cityLng);

        model.addAttribute("cityInput", cityName+", "+regioneName+", "+statoName);
        model.addAttribute("cityName", cityName);
        model.addAttribute("regioneName", regioneName);
        model.addAttribute("statoName", statoName);
        model.addAttribute("cityLat", cityLat);
        model.addAttribute("cityLng", cityLng);
        model.addAttribute("datetime", datetime.format( Constants.DATE_TIME_LOCAL_FORMATTER ));
        model.addAttribute("dataOraNascita", datetime.format( Constants.DATE_TIME_FORMATTER ));

        GiornoOraPosizioneDTO giornoOraPosizioneDTO = new GiornoOraPosizioneDTO(hour, minute, day, month, year, Double.parseDouble(cityLat), Double.parseDouble(cityLng));
        String temaNataleDescrizione = servizioOroscopoDelGiorno.temaNataleDescrizione(giornoOraPosizioneDTO);
        temaNataleDescrizione = temaNataleDescrizione.replace("\n", "<br>");
        model.addAttribute("temaNataleDescrizione", temaNataleDescrizione);

        return "tema-natale";
    }




    @GetMapping("/tema-natale")
    public String temaNatale(Model model) {
        LocalDateTime dataOra = LocalDateTime.of(1970, 1, 1, 0, 0);
        model.addAttribute("datetime", dataOra.format( Constants.DATE_TIME_LOCAL_FORMATTER ));
        return "tema-natale";
    }


    @GetMapping("/oroscopo")
    public String mostraOroscopo(Model model, @ModelAttribute(redirectAttributInfoSubscription) String infoSubscription) {
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = Util.GiornoOraPosizione_OggiRomaOre12();

        String oroscopoDelGiornoDescrizioneOggi = servizioOroscopoDelGiorno.oroscopoDelGiornoDescrizioneOggi(giornoOraPosizioneDTO);
        oroscopoDelGiornoDescrizioneOggi = oroscopoDelGiornoDescrizioneOggi.replace("\n", "<br>");

        List<OroscopoGiornaliero> listOroscopoGiorn = oroscopoGiornalieroService.findAllByDataOroscopoWithoutVideo(Util.OggiOre12());
        List<OroscopoGiornalieroDTO> listOroscopoGiornoDTO = new ArrayList<>();
        for(OroscopoGiornaliero oroscopo : listOroscopoGiorn) {
            OroscopoGiornalieroDTO dto = new OroscopoGiornalieroDTO(oroscopo);
            listOroscopoGiornoDTO.add(dto);
        }
        model.addAttribute("oroscopoDelGiornoDescrizioneOggi", oroscopoDelGiornoDescrizioneOggi);
        model.addAttribute("videos", listOroscopoGiornoDTO);

        // Aggiungi infoSubscription al modello per essere visualizzato nella vista
        model.addAttribute(redirectAttributInfoSubscription, infoSubscription);

        return "oroscopo";
    }


    @GetMapping("/test-invia-email")
    public String inviaEmail(Model model) {
        EmailUtenti emailUtenti = new EmailUtenti();
        emailUtenti.setEmail("matteo.manili@gmail.com");
        emailService.inviaEmailOrosciopoGioraliero(emailUtenti);
        return "index";
    }


    @GetMapping("/"+Constants.DOM_LUNA_SAPIENS_CONFIRM_EMAIL_OROSC_GIORN)
    public String confirmEmailOroscGiorn(@RequestParam(name = "code", required = true) String code, RedirectAttributes redirectAttributes) {
        logger.info("confirmEmailOroscGiorn code: "+code);
        EmailUtenti emailUtenti = emailUtentiRepository.findByConfirmationCode( code ).orElse(null);
        String message = "";
        if(emailUtenti != null && emailUtenti.getConfirmationCode().trim().equals(code.trim())) {
            if(emailUtenti.getDataRegistrazione()== null){
                emailUtenti.setDataRegistrazione(new Date());
            }
            emailUtenti.setSubscription(true);
            emailUtentiRepository.save(emailUtenti);
            message = "Grazie per aver confermato la tua email. Sei ora iscritto al nostro servizio di oroscopo giornaliero con l'indirizzo "+emailUtenti.getEmail()+". " +
                    "Presto riceverai il tuo primo oroscopo nella tua casella di posta.";
        }else{
            message = "Conferma email non riuscita. Registrati di nuovo";
        }
        redirectAttributes.addFlashAttribute(redirectAttributInfoSubscription, message);
        return "redirect:/oroscopo";
    }


    @GetMapping("/"+Constants.DOM_LUNA_SAPIENS_CANCELLA_ISCRIZ_OROSC_GIORN)
    public String cancelEmailOroscGiorn(@RequestParam(name = "code", required = true) String code, RedirectAttributes redirectAttributes) {
        logger.info("cancelEmailOroscGiorn code: "+code);
        EmailUtenti emailUtenti = emailUtentiRepository.findByConfirmationCode( code ).orElse(null);
        String message = "";
        if(emailUtenti != null && emailUtenti.getConfirmationCode().trim().equals(code.trim())) {
            if(emailUtenti.getDataRegistrazione()== null){
                emailUtenti.setDataRegistrazione(new Date());
            }
            emailUtenti.setSubscription(false);
            emailUtentiRepository.save(emailUtenti);
            message = "La tua cancellazione dall'Oroscopo del giorno è avvenuta con successo. Non riceverai più le nostre previsioni giornaliere. " +
                    "Se desideri iscriverti nuovamente in futuro, visita il nostro sito.";
        }else{
            message = "L'indirizzo email non è presente nel sistema.";
        }
        redirectAttributes.addFlashAttribute(redirectAttributInfoSubscription, message);
        return "redirect:/oroscopo";
    }



    @PostMapping("/"+Constants.DOM_LUNA_SAPIENS_SUBSCRIBE_OROSC_GIORN)
    public String subscribe(@RequestParam("email") @Email @NotEmpty String email, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        logger.info("email: "+email);
        Boolean skipEmailSave = (Boolean) request.getAttribute(Constants.SKIP_EMAIL_SAVE);
        if (skipEmailSave != null && skipEmailSave) {
            redirectAttributes.addFlashAttribute(redirectAttributInfoSubscription, "Troppe richieste. Sottoscrizione email negata.");
        }else{
            Object[] result = emailService.salvaEmail( email );
            Boolean success = (Boolean) result[0];
            String message = (String) result[1];
            EmailUtenti emailUtenti = result[2] instanceof EmailUtenti ? (EmailUtenti) result[2] : null;
            if (success && emailUtenti != null) {
                emailService.inviaConfermaEmailOrosciopoGioraliero(emailUtenti);
            }
            redirectAttributes.addFlashAttribute(redirectAttributInfoSubscription, message);
        }
        return "redirect:/oroscopo";
    }




    @Cacheable(value = Constants.VIDEO_CACHE, key = "#videoName")
    @GetMapping("/oroscopo-giornaliero/{videoName}")
    public ResponseEntity<ByteArrayResource> streamVideo(@PathVariable String videoName) throws IOException {
        OroscopoGiornaliero oroscopoGiornaliero = oroscopoGiornalieroService.findByNomeFileVideo(videoName)
                .orElseThrow(() -> new NoSuchElementException("Video not found with name: " + videoName));
        if (oroscopoGiornaliero.getVideo() != null) {
            return Util.VideoResponseEntityByteArrayResource(oroscopoGiornaliero.getVideo());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * lo uso solo per test
     * @param model
     * @return
     */
    @GetMapping("/genera-video")
    public String gerneraVideo(Model model) {
        //scheduledTasks.test_Oroscopo_Segni_Transiti_Aspetti();
        scheduledTasks.creaOroscopoGiornaliero();
        return "index";
    }


    @GetMapping("/info-privacy")
    public String infoPrivacy(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "info-privacy";
    }

    @GetMapping("/termini-di-servizio")
    public String terminiDiServizio(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "termini-di-servizio";
    }


    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        model.addAttribute("infoError", "Errore generale.");
        return "error";
    }


}
