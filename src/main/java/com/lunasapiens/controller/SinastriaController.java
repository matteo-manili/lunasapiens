package com.lunasapiens.controller;

import com.lunasapiens.Constants;
import com.lunasapiens.dto.CoordinateDTO;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.zodiac.BuildInfoAstrologiaAstroSeek;
import com.lunasapiens.zodiac.ServizioSinastria;
import com.theokanning.openai.completion.chat.ChatMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;


@Controller
public class SinastriaController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SinastriaController.class);

    @Autowired
    ServizioSinastria servizioSinastria;

    @Autowired
    private CacheManager cacheManager;


    // #################################### SINATRIA #####################################


    @GetMapping("/sinastria")
    public RedirectView redirect_sinastria() {
        RedirectView redirectView = new RedirectView("/", true);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY); // Imposta il codice 301
        return redirectView;
    }


    /**
     * servizio tema natale
     */
    @GetMapping("/sinastria_OLD")
    public String sinastria(Model model,
                            @ModelAttribute("dateTime") String dateTime,
                            @ModelAttribute("cityInput") String cityInput,
                            @ModelAttribute("cityName") String cityName,
                            @ModelAttribute("regioneName") String regioneName,
                            @ModelAttribute("statoName") String statoName,
                            @ModelAttribute("statoCode") String statoCode,
                            @ModelAttribute("cityLat") String cityLat,
                            @ModelAttribute("cityLng") String cityLng,
                            @ModelAttribute("nome") String nome,

                            @ModelAttribute("dateTime_2") String dateTime_2,
                            @ModelAttribute("cityInput_2") String cityInput_2,
                            @ModelAttribute("cityName_2") String cityName_2,
                            @ModelAttribute("regioneName_2") String regioneName_2,
                            @ModelAttribute("statoName_2") String statoName_2,
                            @ModelAttribute("statoCode_2") String statoCode_2,
                            @ModelAttribute("cityLat_2") String cityLat_2,
                            @ModelAttribute("cityLng_2") String cityLng_2,
                            @ModelAttribute("nome_2") String nome_2,

                            @ModelAttribute("sinastriaDescrizione") String sinastriaDescrizione,
                            @ModelAttribute("paginaChatId") String paginaChatId,
                            @ModelAttribute(Constants.USER_SESSION_ID) String userSessionId,
                            @ModelAttribute("relationship") String relationship
    ) {

        logger.info("sono in sinastria");

        final LocalDateTime defaultDateTime = LocalDateTime.of(1980, 1, 1, 0, 0);

        Optional<String> optionalDateTime = Optional.ofNullable(dateTime);
        optionalDateTime
                .filter(dateTimeString -> !dateTimeString.isEmpty())
                .ifPresentOrElse(
                        presentDateTime -> model.addAttribute("dateTime", presentDateTime),
                        () -> model.addAttribute("dateTime", defaultDateTime.format(Constants.DATE_TIME_LOCAL_FORMATTER)));

        Optional<String> optionalDateTime_2 = Optional.ofNullable(dateTime_2);
        optionalDateTime_2
                .filter(dateTimeString -> !dateTimeString.isEmpty())
                .ifPresentOrElse(
                        presentDateTime -> model.addAttribute("dateTime_2", presentDateTime),
                        () -> model.addAttribute("dateTime_2", defaultDateTime.format(Constants.DATE_TIME_LOCAL_FORMATTER)));


        // Only add attributes if they are not null or empty
        Optional.ofNullable(cityInput).filter(input -> !input.isEmpty()).ifPresent(input -> model.addAttribute("cityInput", input));
        Optional.ofNullable(cityName).filter(name -> !name.isEmpty()).ifPresent(name -> model.addAttribute("cityName", name));
        Optional.ofNullable(regioneName).filter(region -> !region.isEmpty()).ifPresent(region -> model.addAttribute("regioneName", region));
        Optional.ofNullable(statoName).filter(state_name -> !state_name.isEmpty()).ifPresent(state_name -> model.addAttribute("statoName", state_name));
        Optional.ofNullable(statoCode).filter(state_code -> !state_code.isEmpty()).ifPresent(state_code -> model.addAttribute("statoCode", state_code));
        Optional.ofNullable(cityLat).filter(lat -> !lat.isEmpty()).ifPresent(lat -> model.addAttribute("cityLat", lat));
        Optional.ofNullable(cityLng).filter(lng -> !lng.isEmpty()).ifPresent(lng -> model.addAttribute("cityLng", lng));

        Optional.ofNullable(cityInput_2).filter(input_2 -> !input_2.isEmpty()).ifPresent(input_2 -> model.addAttribute("cityInput_2", input_2));
        Optional.ofNullable(cityName_2).filter(name_2 -> !name_2.isEmpty()).ifPresent(name_2 -> model.addAttribute("cityName_2", name_2));
        Optional.ofNullable(regioneName_2).filter(region_2 -> !region_2.isEmpty()).ifPresent(region_2 -> model.addAttribute("regioneName_2", region_2));
        Optional.ofNullable(statoName_2).filter(state_name_2 -> !state_name_2.isEmpty()).ifPresent(state_name_2 -> model.addAttribute("statoName_2", state_name_2));
        Optional.ofNullable(statoCode_2).filter(state_code_2 -> !state_code_2.isEmpty()).ifPresent(state_code_2 -> model.addAttribute("statoCode_2", state_code_2));
        Optional.ofNullable(cityLat_2).filter(lat_2 -> !lat_2.isEmpty()).ifPresent(lat_2 -> model.addAttribute("cityLat_2", lat_2));
        Optional.ofNullable(cityLng_2).filter(lng_2 -> !lng_2.isEmpty()).ifPresent(lng_2 -> model.addAttribute("cityLng_2", lng_2));

        Optional.ofNullable(sinastriaDescrizione).filter(description -> !description.isEmpty()).ifPresent(description -> model.addAttribute("sinastriaDescrizione", description));
        Optional.ofNullable(paginaChatId).filter(id -> !id.isEmpty()).ifPresent(id -> model.addAttribute("paginaChatId", id));
        Optional.ofNullable(userSessionId).filter(id -> !id.isEmpty()).ifPresent(id -> model.addAttribute(Constants.USER_SESSION_ID, id));
        Optional.ofNullable(relationship).filter(id -> !id.isEmpty()).ifPresent(id -> model.addAttribute("relationship", id));

        model.addAttribute("relationshipOptions", Constants.RELATIONSHIP_OPTIONS);

        return "sinastria";
    }



    @GetMapping("/sinastriaSubmit")
    public String sinastriaSubmit(
            RedirectAttributes redirectAttributes, HttpServletRequest request, @RequestParam("relationship") String relationship,
            @RequestParam("dateTime") String dateTimeStr,
            @RequestParam("cityLat") String cityLat,
            @RequestParam("cityLng") String cityLng,
            @RequestParam("cityName") String cityName,
            @RequestParam("regioneName") String regioneName,
            @RequestParam("statoName") String statoName,
            @RequestParam("statoCode") String statoCode,
            @RequestParam("nome") String nome,

            @RequestParam("dateTime_2") String dateTimeStr_2,
            @RequestParam("cityLat_2") String cityLat_2,
            @RequestParam("cityLng_2") String cityLng_2,
            @RequestParam("cityName_2") String cityName_2,
            @RequestParam("regioneName_2") String regioneName_2,
            @RequestParam("statoName_2") String statoName_2,
            @RequestParam("statoCode_2") String statoCode_2,
            @RequestParam("nome_2") String nome_2) {

        logger.info("sono in sinastriaSubmit");

        // VALIDAZIONE DATE TIME
        if (dateTimeStr == null || dateTimeStr.isEmpty() || dateTimeStr_2 == null || dateTimeStr_2.isEmpty()) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Inserisci una data e ora di nascita valide.");
            return "redirect:/sinastria";
        }
        LocalDateTime dateTime; LocalDateTime dateTime_2;
        try {
            dateTime = LocalDateTime.parse(dateTimeStr);
            dateTime_2 = LocalDateTime.parse(dateTimeStr_2);
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Formato data non valido.");
            return "redirect:/sinastria";
        }
        // VALIDAZIONE CITTA' (latitudine e longitudine vuote)
        if (cityLat == null || cityLat.isEmpty() || cityLng == null || cityLng.isEmpty() || cityName == null || cityName.isEmpty() ||
                cityLat_2 == null || cityLat_2.isEmpty() || cityLng_2 == null || cityLng_2.isEmpty() || cityName_2 == null || cityName_2.isEmpty()
        ) {
            redirectAttributes.addFlashAttribute(Constants.INFO_ERROR, "Seleziona una città valida dal suggerimento automatico.");
            return "redirect:/sinastria";
        }





        HttpSession session = request.getSession(true); // Crea una nuova sessione se non esiste
        String userId = (String) session.getAttribute(Constants.USER_SESSION_ID);
        if (userId == null) {
            userId = UUID.randomUUID().toString(); // Genera un nuovo ID solo se non esiste
            session.setAttribute(Constants.USER_SESSION_ID, userId);
        }

        // Estrai le singole componenti della data e ora
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int day = dateTime.getDayOfMonth();
        int month = dateTime.getMonthValue();
        int year = dateTime.getYear();

        int hour_2 = dateTime_2.getHour();
        int minute_2 = dateTime_2.getMinute();
        int day_2 = dateTime_2.getDayOfMonth();
        int month_2 = dateTime_2.getMonthValue();
        int year_2 = dateTime_2.getYear();


        String luogoNascita = String.join(", ", cityName, regioneName, statoName);
        String luogoNascita_2 = String.join(", ", cityName_2, regioneName_2, statoName_2);

        logger.info("Ora: " + hour); logger.info("Minuti: " + minute); logger.info("Giorno: " + day); logger.info("Mese: " + month); logger.info("Anno: " + year);
        logger.info("cityName: " + cityName); logger.info("regioneName: " + regioneName); logger.info("statoName: " + statoName); logger.info("statoCode: " + statoCode);
        logger.info("Latitude: " + cityLat); logger.info("Longitude: " + cityLng); logger.info(Constants.USER_SESSION_ID + userId);

        // Prepara i dati da passare tramite redirectAttributes
        redirectAttributes.addFlashAttribute("cityInput", cityName + ", " + regioneName + ", " + statoName);
        redirectAttributes.addFlashAttribute("cityName", cityName);
        redirectAttributes.addFlashAttribute("regioneName", regioneName);
        redirectAttributes.addFlashAttribute("statoName", statoName);
        redirectAttributes.addFlashAttribute("statoCode", statoCode);
        redirectAttributes.addFlashAttribute("cityLat", cityLat);
        redirectAttributes.addFlashAttribute("cityLng", cityLng);
        redirectAttributes.addFlashAttribute("dateTime", dateTime.format(Constants.DATE_TIME_LOCAL_FORMATTER));
        redirectAttributes.addFlashAttribute("dataOraNascita", dateTime.format(Constants.DATE_TIME_FORMATTER));
        redirectAttributes.addFlashAttribute("luogoNascita", cityName + ", " + regioneName + ", " + statoName);
        redirectAttributes.addFlashAttribute("nome", nome);

        redirectAttributes.addFlashAttribute("cityInput_2", cityName_2 + ", " + regioneName_2 + ", " + statoName_2);
        redirectAttributes.addFlashAttribute("cityName_2", cityName_2);
        redirectAttributes.addFlashAttribute("regioneName_2", regioneName_2);
        redirectAttributes.addFlashAttribute("statoName_2", statoName_2);
        redirectAttributes.addFlashAttribute("statoCode_2", statoCode_2);
        redirectAttributes.addFlashAttribute("cityLat_2", cityLat_2);
        redirectAttributes.addFlashAttribute("cityLng_2", cityLng_2);
        redirectAttributes.addFlashAttribute("dateTime_2", dateTime_2.format(Constants.DATE_TIME_LOCAL_FORMATTER));
        redirectAttributes.addFlashAttribute("dataOraNascita_2", dateTime_2.format(Constants.DATE_TIME_FORMATTER));
        redirectAttributes.addFlashAttribute("luogoNascita_2", cityName_2 + ", " + regioneName_2 + ", " + statoName_2);
        redirectAttributes.addFlashAttribute("nome_2", nome_2);

        GiornoOraPosizioneDTO giornoOraPosizioneDTO = new GiornoOraPosizioneDTO(hour, minute, day, month, year, Double.parseDouble(cityLat), Double.parseDouble(cityLng));
        CoordinateDTO coordinateDTO = new CoordinateDTO(cityName, regioneName, statoName, statoCode);
        StringBuilder sinastria_1 = servizioSinastria.sinastriaDescrizione_AstrologiaAstroSeek(giornoOraPosizioneDTO, coordinateDTO);

        GiornoOraPosizioneDTO giornoOraPosizioneDTO_2 = new GiornoOraPosizioneDTO(hour_2, minute_2, day_2, month_2, year_2, Double.parseDouble(cityLat_2), Double.parseDouble(cityLng_2));
        CoordinateDTO coordinateDTO_2 = new CoordinateDTO(cityName_2, regioneName_2, statoName_2, statoCode_2);
        StringBuilder sinastria_2 = servizioSinastria.sinastriaDescrizione_AstrologiaAstroSeek(giornoOraPosizioneDTO_2, coordinateDTO_2);

        StringBuilder significatiTemaNatale = servizioSinastria.significatiSinastriaDescrizione();

        String descrizioneTemaNatalePage = new StringBuilder()
            .append("<hr class=\"flex-grow-1 border-3\"><span class=\"mx-2 fw-bold\">Tema Natale 1</span><hr class=\"flex-grow-1 border-3\">")
            .append(sinastria_1.toString())  // Usa il valore attuale di sinastria_1
            .append("<hr class=\"flex-grow-1 border-3\"><span class=\"mx-2 fw-bold\">Tema Natale 2</span><hr class=\"flex-grow-1 border-3\">")
            .append(sinastria_2.toString())  // Usa il valore attuale di sinastria_2
            .append("<hr class=\"flex-grow-1 border-3\"><span class=\"mx-2 fw-bold\">Significati</span><hr class=\"flex-grow-1 border-3\">")
            .append(significatiTemaNatale).toString();


        redirectAttributes.addFlashAttribute("sinastriaDescrizione", descrizioneTemaNatalePage);
        String paginaChatId = UUID.randomUUID().toString();
        redirectAttributes.addFlashAttribute("paginaChatId", paginaChatId);
        redirectAttributes.addFlashAttribute(Constants.USER_SESSION_ID, userId);

        // Metto in cache i chatMessageIa
        Cache cache = cacheManager.getCache(Constants.MESSAGE_BOT_CACHE);
        if (cache == null) {
            logger.error("Cache not found: " + Constants.MESSAGE_BOT_CACHE);
            return "redirect:/sinastria";
        }
        List<ChatMessage> chatMessageIa = new ArrayList<>();
        StringBuilder sinastriaDescIstruzioniBOTSystem = BuildInfoAstrologiaAstroSeek.sinastriaIstruzioneBOTSystem(relationship, nome, nome_2,
                sinastria_1.toString(), sinastria_2.toString(), significatiTemaNatale.toString(), dateTime, dateTime_2, luogoNascita, luogoNascita_2);

        logger.info( "sinastriaDescrizioneIstruzioneBOTSystem: "+sinastriaDescIstruzioniBOTSystem );
        chatMessageIa.add(new ChatMessage("system", sinastriaDescIstruzioniBOTSystem.toString() ));
        cache.put(paginaChatId, chatMessageIa);

        return "redirect:/sinastria";
    }









}
