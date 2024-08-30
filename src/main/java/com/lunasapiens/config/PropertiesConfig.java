package com.lunasapiens.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class PropertiesConfig {

    @Bean
    public Properties omeopatiaElementi(){ return getProperties( "omeopatia-elementi.properties" ); }

    @Bean
    public Properties lunaSegni(){ return getProperties( "luna-segni.properties" ); }

    @Bean
    public Properties segniAscendente(){ return getProperties( "segni-ascendente.properties" ); }

    @Bean
    public Properties pianetiCaseSignificato() { return getProperties("pianeti-case-significato.properties"); }

    @Bean
    public Properties caseSignificato() {
        return getProperties("case-significato.properties");
    }

    @Bean
    public Properties pianetiOroscopoSignificato() { return getProperties("pianeti-oroscopo-significato.properties"); }

    @Bean
    public Properties pianetaRetrogrado() {
        return getProperties("pianeta-retrogrado.properties");
    }

    @Bean
    public Properties aspettiPianeti() { return getProperties("aspetti-pianeti.properties"); }

    @Bean
    public Properties segniZodiacali() { return getProperties("segni-zodiacali.properties"); }

    // per oroscopo dell giorno
    @Bean
    public Properties transitiSegniPianeti_OroscopoDelGiorno() { return getProperties("transiti-segni-pianeti_OG.properties"); }

    // per Tema Natale
    @Bean
    public Properties transitiPianetiSegni_TemaNatale() { return getProperties("transiti-pianeti-segni_TN.properties"); }



    private Properties getProperties(String fileNameProperties) {
        Properties properties = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/"+fileNameProperties)) {
            if (is != null) {
                properties.load(is);
            } else {
                throw new FileNotFoundException("File properties non trovato");
            }
        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento del file properties: "+fileNameProperties, e);
        }
        return properties;
    }



}

