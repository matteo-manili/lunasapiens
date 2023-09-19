package com.lunasapiens;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "Benvenuto!");
        // Puoi eseguire operazioni o passare dati al model qui se necessario
        return "index"; // Assumi che "index" sia il nome del tuo template HTML (senza estensione)
    }

    @GetMapping("/index")
    public String index_2(Model model) {
        model.addAttribute("message", "Benvenuto!");
        // Puoi eseguire operazioni o passare dati al model qui se necessario
        return "index"; // Assumi che "index" sia il nome del tuo template HTML (senza estensione)
    }




}
