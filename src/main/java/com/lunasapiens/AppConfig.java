package com.lunasapiens;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


@Configuration
@ComponentScan(basePackages = "com.lunasapiens")
public class AppConfig implements WebMvcConfigurer {

    @Autowired
    private Environment env;


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Bean
    public FacebookConfig getfacebookConfig() {
        FacebookConfig facebookConfig;
        try{

            facebookConfig = new FacebookConfig(
                    env.getProperty("api.facebook.version"),
                    env.getProperty("api.facebook.appid"),
                    env.getProperty("api.facebook.appsecret"),
                    env.getProperty("api.facebook.accesstoken"),
                    env.getProperty("api.facebook.pageaccesstoken"));


        } catch (IllegalArgumentException e) {
            // In caso di eccezione, utilizza il file di configurazione esterno
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream("C:/intellij_work/lunasapiens-application-db.properties")) {
                properties.load(fis);


                facebookConfig = new FacebookConfig(
                        properties.getProperty("api.facebook.version"),
                        properties.getProperty("api.facebook.appid"),
                        properties.getProperty("api.facebook.appsecret"),
                        properties.getProperty("api.facebook.accesstoken"),
                        properties.getProperty("api.facebook.pageaccesstoken"));


            } catch (IOException ioException) {
                throw new RuntimeException("Errore nella lettura del file di configurazione esterno.", ioException);
            }
        }
        return facebookConfig;
    }


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


    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        try{
            dataSource.setUrl(env.getProperty("spring.datasource.url"));
            dataSource.setUsername(env.getProperty("spring.datasource.username"));
            dataSource.setPassword(env.getProperty("spring.datasource.password"));
            //System.out.println("111 Versione di PostgreSQL: " + getPostgreSQLVersion(dataSource));
        } catch (IllegalArgumentException e) {
            // In caso di eccezione, utilizza il file di configurazione esterno
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream("C:/intellij_work/lunasapiens-application-db.properties")) {
                properties.load(fis);
                dataSource.setUrl(properties.getProperty("spring.datasource.url"));
                dataSource.setUsername(properties.getProperty("spring.datasource.username"));
                dataSource.setPassword(properties.getProperty("spring.datasource.password"));
                //System.out.println("222 Versione di PostgreSQL: " + getPostgreSQLVersion(dataSource));
            } catch (IOException ioException) {
                throw new RuntimeException("Errore nella lettura del file di configurazione esterno.", ioException);
            }
        }
        return dataSource;
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


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/static/**") // Percorso URL delle risorse statiche
                .addResourceLocations("classpath:/static/", "file:src/main/resources/static/")
                .setCachePeriod(0); // Disabilita la cache

        registry
                .addResourceHandler("/static/oroscopo_giornaliero/**") // Percorso URL delle risorse statiche
                .addResourceLocations("classpath:/static/oroscopo_giornaliero/", "file:src/main/resources/static/oroscopo_giornaliero/")
                .setCachePeriod(0);

    }





}

