package com.lunasapiens;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "index";
    }


    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }


    @GetMapping("/index")
    public String index_2(Model model) {
        model.addAttribute("message", "Benvenuto!");
        // Puoi eseguire operazioni o passare dati al model qui se necessario
        return "index"; // Assumi che "index" sia il nome del tuo template HTML (senza estensione)
    }




}
