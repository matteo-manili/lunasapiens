package com.lunasapiens;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Properties;


@Configuration
@ComponentScan(basePackages = {"com.lunasapiens", "com.lunasapiens.zodiac"})
@EnableCaching
public class AppConfig implements WebMvcConfigurer {

    @Autowired
    private Environment env;

    @Bean
    public Properties pianetaRetrogrado() {
        Properties properties = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/pianeta-retrogrado.properties")) {
            if (is != null) {
                properties.load(is);
            } else {
                throw new FileNotFoundException("File properties non trovato");
            }
        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento del file properties", e);
        }
        return properties;
    }

    @Bean
    public Properties AspettiPianeti() {
        Properties properties = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/aspetti-pianeti.properties")) {
            if (is != null) {
                properties.load(is);
            } else {
                throw new FileNotFoundException("File properties non trovato");
            }
        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento del file properties", e);
        }
        return properties;
    }

    @Bean
    public Properties segniZodiacali() {
        Properties properties = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/segni-zodiacali.properties")) {
            if (is != null) {
                properties.load(is);
            } else {
                throw new FileNotFoundException("File properties non trovato");
            }
        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento del file properties", e);
        }
        return properties;
    }

    @Bean
    public Properties transitiPianetiSegni() {
        Properties properties = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/transiti-pianeti-segni.properties")) {
            if (is != null) {
                properties.load(is);
            } else {
                throw new FileNotFoundException("File properties non trovato");
            }
        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento del file properties", e);
        }
        return properties;
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
    public OpenAiGptConfig getParamOpenAi() {
        String apiKeyOpenAI = "";
        try{
            apiKeyOpenAI = env.getProperty("api.key.openai");
            //System.out.println("111 api_openai: " + apiKeyOpenAI);
        } catch (IllegalArgumentException e) {
            // In caso di eccezione, utilizza il file di configurazione esterno
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream("C:/intellij_work/lunasapiens-application-db.properties")) {
                properties.load(fis);
                apiKeyOpenAI = properties.getProperty("api.key.openai");
                //System.out.println("222 api_openai: " + apiKeyOpenAI);
            } catch (IOException ioException) {
                throw new RuntimeException("Errore nella lettura del file di configurazione esterno.", ioException);
            }
        }
        OpenAiGptConfig openAiGptConfig = new OpenAiGptConfig(apiKeyOpenAI,
                env.getProperty("api.openai.model.gpt.4"),
                env.getProperty("api.openai.model.gpt.3.5"),
                env.getProperty("api.openai.model.gpt.3.5.turbo.instruct"));
        return openAiGptConfig;
    }



    @Bean
    public JavaMailSender javaMailSender() {
        if (isLocalhost()) {
            // Configurazione per ambiente di sviluppo (localhost)
            return javaMailSenderGmailDev();
        } else {
            // Configurazione per ambiente di produzione
            return javaMailSenderLunaSapiensProd();
        }
    }


    private JavaMailSender javaMailSenderGmailDev() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setUsername(env.getProperty("gmail.mail.username"));
        mailSender.setPassword(env.getProperty("gmail.mail.password"));
        mailSender.setHost(env.getProperty("gmail.mail.smtp.host"));
        mailSender.setPort(Integer.parseInt(env.getProperty("mail.smtp.port")));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", env.getProperty("mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", env.getProperty("mail.smtp.starttls.enable"));
        props.put("mail.debug", env.getProperty("mail.debug"));

        // questa impoistazion evita errori di certificato.
        // Questa impostazione configura il client JavaMail per fidarsi specificamente del certificato SSL fornito dal server smtp.gmail.comdurantesmtp.gmail.com
        // senza ulteriori validazioni della catena di certificati.
        // È una soluzione più sicura rispetto a disabilitare completamente la validazione: props.put("mail.smtp.ssl.trust", "*");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        mailSender.setJavaMailProperties(props);
        return mailSender;
    }


    private JavaMailSender javaMailSenderLunaSapiensProd() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setUsername(env.getProperty("mail.username"));
        mailSender.setPassword(env.getProperty("mail.password"));
        mailSender.setHost(env.getProperty("mail.smtp.host"));
        mailSender.setPort(Integer.parseInt(env.getProperty("mail.smtp.port")));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", env.getProperty("mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", env.getProperty("mail.smtp.starttls.enable"));
        props.put("mail.debug", env.getProperty("mail.debug"));

        mailSender.setJavaMailProperties(props);
        return mailSender;
    }


    public boolean isLocalhost() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            System.out.println("Nome host: " + localhost);

            if( localhost.toString().contains("DESKTOP-MATTEO") ){
                System.out.println("Ambiente rilevato: DEV");
                return true;
            }else{
                System.out.println("Ambiente rilevato: PROD");
                return false;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return true; // Ritorna true se si verifica un'eccezione (es. per sicurezza in sviluppo)
        }
    }


    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofDays(1))
                .maximumSize(30)); // massimo numero di elementi
        return cacheManager;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
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

