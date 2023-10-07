package com.lunasapiens;


import com.lunasapiens.zodiac.ServiziAstrologici;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class IndexController {

    @Autowired
    private AppConfig appConfig;

    @GetMapping("/")
    public String index(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("message", "Welcome to our dynamic landing page!");
        return "index";
    }


    @GetMapping("/info-privacy")
    public String infoPrivacy(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "info-privacy";
    }


    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @GetMapping("/oroscopo")
    public String oroscopo(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {

        //09-03-1996 ore 21

        
        //
        //Latitudine di Palermo	38.1156879
        //Longitudine di Palermo	13.3612671

        int ora = 21; int minuti = 0;
        int giorno = 9; int mese = 3; int anno = 1996;
        double lon = 38.1; double lat = 13.3;

        /*

        int ora = 12; int minuti = 0;
        int giorno = 30; int mese = 9; int anno = 2023;
        double lon = 49.9; double lat = 12.4;

         */

        ServiziAstrologici sA = new ServiziAstrologici(appConfig.getKeyOpenAi());
        StringBuilder sB = sA.servizioOroscopoDelGiorno(Constants.segniZodiacali().get(9), ora, minuti, giorno, mese, anno, lon, lat);

        //servizioOroscopoDelGiorno




        model.addAttribute("oroscopoGpt", sB  );
        return "oroscopo";
    }





}
