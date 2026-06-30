package com.lunasapiens.service;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class EnvironmentUtils {


    private final Environment environment;


    public EnvironmentUtils(Environment environment){
        this.environment = environment;
    }


    public boolean isProduction(){

        return Arrays.asList(
                environment.getActiveProfiles()
        ).contains("prod");

    }


    public boolean isDevelopment(){

        return Arrays.asList(
                environment.getActiveProfiles()
        ).contains("dev");

    }

}
