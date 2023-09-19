package com.lunasapiens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class LunasapiensApplication {

    public static void main(String[] args) {
        SpringApplication.run(LunasapiensApplication.class, args);
    }

    @GetMapping("/")
    public String homePage(@RequestParam(value = "myName", defaultValue = "World") String name) {
        return String.format("HOME PAGE AAAAA", name);
    }
    /// ciao

    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }
}
