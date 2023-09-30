package com.lunasapiens;



import at.kugel.zodiac.TextHoroscop;
import at.kugel.zodiac.house.HousePlacidus;
import com.lunasapiens.zodiac.*;
import at.kugel.zodiac.planet.PlanetAA0;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.Choice;
import com.azure.ai.openai.models.Completions;
import com.azure.ai.openai.models.CompletionsOptions;
import com.azure.core.credential.KeyCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private ZodiacConfig zodiacConfig;

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

        int giorno = 30; int mese = 9; int anno = 2023;
        double lon = 49.9; double lat = 12.4;
        int ora = 12; int minuti = 0;

        String horoscop = TextHoroscop(ora, minuti, giorno, mese, anno, lon, lat);
        BuildInfoAstrologia buildInfoAstrologia = new BuildInfoAstrologia();
        //System.out.println("############################ setOroscopoBase ###################################");
        OroscopoBase aa = buildInfoAstrologia.setOroscopoBase(horoscop);
        //System.out.println( aa.toString() );

        System.out.println("############################ setCasePlacide ###################################");
        ArrayList<CasePlacide> bb = buildInfoAstrologia.setCasePlacide(horoscop);
        for(CasePlacide var : bb){
            System.out.println( var.toString() );
        }
        System.out.println("############################ setPianetiAspetti ###################################");
        ArrayList<PianetiAspetti> cc = buildInfoAstrologia.setPianetiAspetti(horoscop);
        for(PianetiAspetti var : cc){
            System.out.println( var.toString() );
        }


        System.out.println("############################ TEXT IA ###################################");
        String multilineString = "Crea l'oroscopo del giorno (di massimo 200 parole) per il segno del "+SegniZodiacali.segni().get(10) +" in base a questi dati \n" +
            "Il giorno di oggi è: "+giorno+ "/" +mese+ "/" +anno+ " ore"+ora+":"+minuti+ "\n"+

            "Transiti o anche detti aspetti dei pianeti: " + "\n";
        for(PianetiAspetti var : cc){
            multilineString += var.toString();
            //System.out.println( var.toString() );
        }

        multilineString += "\n" + "Case Placide: " + "\n";
        for(CasePlacide var : bb){
            multilineString += var.toString();
            //System.out.println( var.toString() );
        }

        System.out.println( multilineString );


        model.addAttribute("result", multilineString );



        // @@@@@@@@@@@@@@@@ OPENAI Azure @@@@@@@@@@@@@@@@@@@@@@


        String apiKey = zodiacConfig.getKeyOpenAi();
        System.out.println("API Key di OpenAI: " + apiKey);

        OpenAIClient client = new OpenAIClientBuilder().credential(new KeyCredential( apiKey )).buildClient();

        List<String> prompt = new ArrayList<>();

        prompt.add( multilineString );
        // gpt-3.5-turbo-instruct
        // davinci-002
        Completions completions = client.getCompletions("gpt-3.5-turbo-instruct", new CompletionsOptions(prompt)
                .setMaxTokens(1500).setTemperature(0.5));

        System.out.printf("Model ID=%s is created at %s.%n", completions.getId(), completions.getCreatedAt());

        StringBuilder textContent = new StringBuilder();
        for (Choice choice : completions.getChoices()) {
            System.out.printf("Index: %d, Text: %s.%n", choice.getIndex(), choice.getText());
            textContent.append(choice.getText()).append("\n");
        }

        model.addAttribute("oroscopoGpt", textContent.toString()  );



        return "oroscopo";
    }


    // Roma 49.9 e 12.4 --- Pisa 43.7 e 10.4
    private String TextHoroscop(int ora, int minuti, int giorno, int mese, int anno, double lon, double lat){


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
        double orario = (ora + (minuti / 60.0)) / 24.0;
        horoscop.setTime(giorno, mese, anno, orario);
        // set your user data location value
        // Pisa 43.7 e 10.4 --- Roma 49.9 e 12.4
        horoscop.setLocationDegree(lon, lat);
        // calculate the values
        horoscop.calcValues();
        // do something with the data or output raw data
        System.out.println(horoscop.toString());

        return horoscop.toString();
    }


}
