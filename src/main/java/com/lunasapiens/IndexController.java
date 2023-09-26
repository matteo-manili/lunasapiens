package com.lunasapiens;



import at.kugel.zodiac.TextHoroscop;
import at.kugel.zodiac.house.HousePlacidus;
import at.kugel.zodiac.planet.PlanetAA0;
import at.kugel.zodiac.planet.PlanetInt;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.Choice;
import com.azure.ai.openai.models.Completions;
import com.azure.ai.openai.models.CompletionsOptions;
import com.azure.core.credential.KeyCredential;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;

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
        double orario = (6 + (31 / 60.0)) / 24.0;
        horoscop.setTime(26, 9, 2023, orario);
        // set your user data location value
        // Pisa 43.7 e 10.4 --- Roma 49.9 e 12.4
        horoscop.setLocationDegree(49.9, 12.4);
        // calculate the values
        horoscop.calcValues();
        // do something with the data or output raw data
        System.out.println(horoscop.toString());


        model.addAttribute("result", horoscop.toString()  );

        // @@@@@@@@@@@@@@@@@@ KEY @@@@@@@@@@@@@@@@@

        // test 1: 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
        // test 2: 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

        // @@@@@@@@@@@@@@@@ OPENAI API TheoKanning @@@@@@@@@@@@@@@@@@@@@@
        /*
        OpenAiService service = new OpenAiService("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");
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt("Somebody once told me the world is gonna roll me")
                .model("ada")
                .echo(true)
                .build();
        service.createCompletion(completionRequest).getChoices().forEach(System.out::println);
        */

        // @@@@@@@@@@@@@@@@ OPENAI Azure @@@@@@@@@@@@@@@@@@@@@@


        /*
        OpenAIClient client = new OpenAIClientBuilder()
                .credential(new KeyCredential("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"))
                .buildClient();

        List<String> prompt = new ArrayList<>();
        //prompt.add("Fammi l'orosoco del giorno sul mio segno zodiacale che è l'acquario, oggi è 26 settembre 2023 " +
          //      "e basati su questi dati astrologici/astronomi che riguardano il mio tema natale alla mia nascira : "+horoscop.toString());

        prompt.add(" " +
                "in base a questi dati, mi dici Urano e Nettuno in che segno si trovano? : "+horoscop.toString());
        // gpt-3.5-turbo-instruct
        // davinci-002
        Completions completions = client.getCompletions("gpt-3.5-turbo-instruct", new CompletionsOptions(prompt)
                .setMaxTokens(500).setTemperature(0d));

        System.out.printf("Model ID=%s is created at %s.%n", completions.getId(), completions.getCreatedAt());

        StringBuilder textContent = new StringBuilder();
        for (Choice choice : completions.getChoices()) {
            System.out.printf("Index: %d, Text: %s.%n", choice.getIndex(), choice.getText());
            textContent.append(choice.getText()).append("\n");
        }

        model.addAttribute("oroscopoGpt", textContent.toString()  );
*/




        return "oroscopo";
    }






}
