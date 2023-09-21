package com.lunasapiens;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


@Configuration
// in questo modo può leggere il file application-local.properties. per attivare il servizio spring profiles ho inserito
// nel file application.properties la riga "spring.profiles.active=local", spring sa che dovrà leggere il application-local.properties
// perché contiene "local" nel nome
//@PropertySource("classpath:application-${spring.profiles.active}.properties")

//@PropertySource("file:C:/intellij_work/lunasapiens-application-db.properties")


public class DatabaseConfig {

    /*
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;
*/


    @Autowired
    private Environment env;


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        try{
            dataSource.setUrl(env.getProperty("spring.datasource.url"));
            dataSource.setUsername(env.getProperty("spring.datasource.username"));
            dataSource.setPassword(env.getProperty("spring.datasource.password"));

        } catch (Exception e) {
            // In caso di eccezione, utilizza il file di configurazione esterno
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream("C:/intellij_work/lunasapiens-application-db.properties")) {
                properties.load(fis);
                dataSource.setUrl(properties.getProperty("spring.datasource.url"));
                dataSource.setUsername(properties.getProperty("spring.datasource.username"));
                dataSource.setPassword(properties.getProperty("spring.datasource.password"));
            } catch (IOException ioException) {
                throw new RuntimeException("Errore nella lettura del file di configurazione esterno.", ioException);
            }
        }

        return dataSource;
    }
}

