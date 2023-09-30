package com.lunasapiens;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


@Configuration
public class ZodiacConfig {

    @Autowired
    private Environment env;


    @Bean
    public String getKeyOpenAi() {
        String api = "";
        try{
            api = env.getProperty("api.key.openai");
            System.out.println("111 api_openai: " + api);
        } catch (IllegalArgumentException e) {
            // In caso di eccezione, utilizza il file di configurazione esterno
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream("C:/intellij_work/lunasapiens-application-db.properties")) {
                properties.load(fis);
                api = properties.getProperty("api.key.openai");
                System.out.println("222 api_openai: " + api);
            } catch (IOException ioException) {
                throw new RuntimeException("Errore nella lettura del file di configurazione esterno.", ioException);
            }
        }
        return api;
    }


    // Metodo per ottenere la versione di PostgreSQL dal database
    private String getPostgreSQLVersion(DataSource dataSource) {
        String version = null;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT version()")) {

            if (resultSet.next()) {
                version = resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return version;
    }


}

