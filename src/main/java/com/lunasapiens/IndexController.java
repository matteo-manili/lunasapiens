package com.lunasapiens;



import at.kugel.zodiac.TextHoroscop;
import at.kugel.zodiac.house.HousePlacidus;
import at.kugel.zodiac.planet.PlanetAA0;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("message", "Welcome to our dynamic landing page!");
        return "index";
    }


    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @GetMapping("/oroscopo")
    public String oroscopo(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {


        // ottiene un'istanza di oroscopo
        // TextHoroscop horoscop = new TextHoroscop ();
        // imposta    l'oroscopo dell'algoritmo di calcolo della posizione del pianeta desiderato . setPlanet ( nuovo PianetaAA0 ());
        // imposta l'algoritmo di calcolo del sistema domestico desiderato
        // può essere qualsiasi cosa dal pacchetto at.kugel.zodiac.house.    oroscopo . setHouse ( nuova CasaPlacidus ());
        // imposta il valore temporale dei dati utente    oroscopo . setTime ( 1 , 12 , 1953 , 20);

        // imposta il valore della posizione dei dati utente    oroscopo . setLocationDegree (- 16 - 17.0 / 60 , 48 + 4.0 / 60 );
        // calcola i valori    dell'oroscopo . valoricalc ();
        // fa qualcosa con i dati, ad esempio genera dati grezzi System . fuori . println ( oroscopo . toString ());


        // get a horoscop instance
        final TextHoroscop horoscop = new TextHoroscop();
        // set your desired planet position calculation algorithm
        horoscop.setPlanet(new PlanetAA0());
        // set your desired house system calculation algorithm
        // may be anything from the at.kugel.zodiac.house package.
        horoscop.setHouse(new HousePlacidus());
        // set your user data time value
        horoscop.setTime(23, 01, 1981, 12);
        // set your user data location value
        // Pisa 43.7 e 10.4 --- Roma 49.9 e 12.4
        horoscop.setLocationDegree(49.9, 12.4);
        // calculate the values
        horoscop.calcValues();
        // do something with the data or output raw data
        System.out.println(horoscop.toString());



        model.addAttribute("result", horoscop.toString()  );
        return "oroscopo";
    }






}
