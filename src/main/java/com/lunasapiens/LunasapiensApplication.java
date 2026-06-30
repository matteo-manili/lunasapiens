package com.lunasapiens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class LunasapiensApplication {

    public static void main(String[] args) {
        SpringApplication.run(LunasapiensApplication.class, args);
    }

}
