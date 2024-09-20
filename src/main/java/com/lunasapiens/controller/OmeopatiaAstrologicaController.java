package com.lunasapiens.controller;

import com.lunasapiens.Constants;
import com.lunasapiens.dto.CoordinateDTO;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.zodiac.ServizioOmeopatiaAstrologica;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;


@Controller
public class OmeopatiaAstrologicaController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(OmeopatiaAstrologicaController.class);

    @Autowired
    ServizioOroscopoDelGiorno servizioOroscopoDelGiorno;

    @Autowired
    ServizioOmeopatiaAstrologica servizioOmeopatiaAstrologica;


    /**
     * servizio omeopatia Astrologica
     */
    @GetMapping("/omeopatia-astrologica")
    public String omeopatiaAstrologica(Model model, @ModelAttribute("dateTime") String datetime,
                                       @ModelAttribute("cityInput") String cityInput,
                                       @ModelAttribute("cityName") String cityName,
                                       @ModelAttribute("regioneName") String regioneName,
                                       @ModelAttribute("statoName") String statoName,
                                       @ModelAttribute("statoCode") String statoCode,
                                       @ModelAttribute("cityLat") String cityLat,
                                       @ModelAttribute("cityLng") String cityLng,
                                       @ModelAttribute("temaNataleDescrizione") String omeopatiaAstrologicaDescrizione,
                                       @AuthenticationPrincipal UserDetails userDetails
    ) {

        logger.info("sono in omeopatia-astrologica");

        LocalDateTime defaultDateTime = LocalDateTime.of(1980, 1, 1, 0, 0);
        Optional<String> optionalDateTime = Optional.ofNullable(datetime);
        optionalDateTime
                .filter(dateTimeString -> !dateTimeString.isEmpty())
                .ifPresentOrElse(
                        presentDateTime -> model.addAttribute("dateTime", presentDateTime),
                        () -> model.addAttribute("dateTime", defaultDateTime.format(Constants.DATE_TIME_LOCAL_FORMATTER)));

        // Only add attributes if they are not null or empty
        Optional.ofNullable(cityInput).filter(input -> !input.isEmpty()).ifPresent(input -> model.addAttribute("cityInput", input));
        Optional.ofNullable(cityName).filter(name -> !name.isEmpty()).ifPresent(name -> model.addAttribute("cityName", name));
        Optional.ofNullable(regioneName).filter(region -> !region.isEmpty()).ifPresent(region -> model.addAttribute("regioneName", region));
        Optional.ofNullable(statoName).filter(state_name -> !state_name.isEmpty()).ifPresent(state_name -> model.addAttribute("statoName", state_name));
        Optional.ofNullable(statoCode).filter(state_code -> !state_code.isEmpty()).ifPresent(state_code -> model.addAttribute("statoCode", state_code));
        Optional.ofNullable(cityLat).filter(lat -> !lat.isEmpty()).ifPresent(lat -> model.addAttribute("cityLat", lat));
        Optional.ofNullable(cityLng).filter(lng -> !lng.isEmpty()).ifPresent(lng -> model.addAttribute("cityLng", lng));
        Optional.ofNullable(omeopatiaAstrologicaDescrizione).filter(description -> !description.isEmpty()).ifPresent(description -> model.addAttribute("omeopatiaAstrologicaDescrizione", description));

        return "omeopatia-astrologica";
    }




    @GetMapping("/omeopatiaAstrologicaSubmit")
    public String omeopatiaAstrologicaSubmit(@RequestParam("dateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime,
                                   @RequestParam("cityLat") String cityLat,
                                   @RequestParam("cityLng") String cityLng,
                                   @RequestParam("cityName") String cityName,
                                   @RequestParam("regioneName") String regioneName,
                                   @RequestParam("statoName") String statoName,
                                   @RequestParam("statoCode") String statoCode,
                                   RedirectAttributes redirectAttributes) {

        logger.info("sono in omeopatiaAstrologicaSubmit");

        // Estrai le singole componenti della data e ora
        int hour = datetime.getHour();
        int minute = datetime.getMinute();
        int day = datetime.getDayOfMonth();
        int month = datetime.getMonthValue();
        int year = datetime.getYear();

        String luogoNascita = String.join(", ", cityName, regioneName, statoName);

        logger.info("Ora: " + hour); logger.info("Minuti: " + minute); logger.info("Giorno: " + day); logger.info("Mese: " + month); logger.info("Anno: " + year);
        logger.info("cityName: " + cityName); logger.info("regioneName: " + regioneName); logger.info("statoName: " + statoName); logger.info("statoCode: " + statoCode);
        logger.info("Latitude: " + cityLat); logger.info("Longitude: " + cityLng);

        // Prepara i dati da passare tramite redirectAttributes
        redirectAttributes.addFlashAttribute("cityInput", cityName + ", " + regioneName + ", " + statoName);
        redirectAttributes.addFlashAttribute("cityName", cityName);
        redirectAttributes.addFlashAttribute("regioneName", regioneName);
        redirectAttributes.addFlashAttribute("statoName", statoName);
        redirectAttributes.addFlashAttribute("statoCode", statoCode);
        redirectAttributes.addFlashAttribute("cityLat", cityLat);
        redirectAttributes.addFlashAttribute("cityLng", cityLng);
        redirectAttributes.addFlashAttribute("dateTime", datetime.format(Constants.DATE_TIME_LOCAL_FORMATTER));
        redirectAttributes.addFlashAttribute("dataOraNascita", datetime.format(Constants.DATE_TIME_FORMATTER));
        redirectAttributes.addFlashAttribute("luogoNascita", cityName + ", " + regioneName + ", " + statoName);

        final GiornoOraPosizioneDTO giornoOraPosizioneDTO = new GiornoOraPosizioneDTO(hour, minute, day, month, year, Double.parseDouble(cityLat), Double.parseDouble(cityLng));
        CoordinateDTO coordinateDTO = new CoordinateDTO(cityName, regioneName, statoName, statoCode);
        StringBuilder omeopatiaAstrologicaDescrizione = servizioOmeopatiaAstrologica.omeopatiaAstrologicaDescrizione_AstrologiaAstroSeek(giornoOraPosizioneDTO, coordinateDTO);
        redirectAttributes.addFlashAttribute("omeopatiaAstrologicaDescrizione", omeopatiaAstrologicaDescrizione.toString());


        return "redirect:/omeopatia-astrologica";
    }


}
