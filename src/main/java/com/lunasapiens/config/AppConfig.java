package com.lunasapiens.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.lunasapiens.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


@Configuration
@ComponentScan(basePackages = {"com.lunasapiens", "com.lunasapiens.zodiac", "com.lunasapiens.filter", "com.lunasapiens.service"})
@EnableCaching
public class AppConfig implements WebMvcConfigurer {

    @Autowired
    private Environment env;

    @Autowired
    private ApplicationContext applicationContext;


    @Bean
    public JwtElements.JwtKeys getJwtRsaKeys() {
        if (Utils.isLocalhost()) {
            List<String> loadPorpoerty = Utils.loadPropertiesEsternoLunaSapiens( new ArrayList<String>(Arrays.asList("jwt.rsa.public.key", "jwt.rsa.private.key")));
            return new JwtElements.JwtKeys(loadPorpoerty.get(0), loadPorpoerty.get(1));
        }else{
            return new JwtElements.JwtKeys(env.getProperty("jwt.rsa.public.key"), env.getProperty("jwt.rsa.private.key") );
        }
    }


    @Bean
    public ApiGeonamesConfig getApiGeonames() {
        if (Utils.isLocalhost()) {
            List<String> loadPorpoerty = Utils.loadPropertiesEsternoLunaSapiens( new ArrayList<String>(Arrays.asList("api.geonames.username")) );
            return new ApiGeonamesConfig(loadPorpoerty.get(0)) ;
        }else{
            return new ApiGeonamesConfig( env.getProperty("api.geonames.username") ) ;
        }
    }


    @Bean
    public FacebookConfig getfacebookConfig() {
        FacebookConfig facebookConfig;
        if (Utils.isLocalhost()) {
            List<String> loadPorpoerty = Utils.loadPropertiesEsternoLunaSapiens( new ArrayList<String>(Arrays.asList(
                    "api.facebook.appid",
                    "api.facebook.appsecret",
                    "api.facebook.idpage" )) );
            facebookConfig = new FacebookConfig(loadPorpoerty.get(0), loadPorpoerty.get(1), loadPorpoerty.get(2));
        }else{
            facebookConfig = new FacebookConfig( env.getProperty("api.facebook.appid"), env.getProperty("api.facebook.appsecret"), env.getProperty("api.facebook.idpage") );
        }
        return facebookConfig;
    }


    @Bean
    public TelegramConfig getParamTelegram() {
        if (Utils.isLocalhost()) {
            List<String> loadPorpoerty = Utils.loadPropertiesEsternoLunaSapiens( new ArrayList<String>(Arrays.asList("api.telegram.token", "api.telegram.chatId",
                    "api.telegram.bot.username")) );
            return new TelegramConfig(loadPorpoerty.get(0), loadPorpoerty.get(1), loadPorpoerty.get(2));
        }else{
            return new TelegramConfig(env.getProperty("api.telegram.token") , env.getProperty("api.telegram.chatId"), env.getProperty("api.telegram.bot.username"));
        }
    }


    @Bean
    public OpenAiGptConfig getParamOpenAi() {
        String apiKeyOpenAI = "";
        try{
            apiKeyOpenAI = env.getProperty("api.key.openai");
            //System.out.println("111 api_openai: " + apiKeyOpenAI);
        } catch (IllegalArgumentException e) {
            // In caso di eccezione, utilizza il file di configurazione esterno. Prima che sviluppassio il metodo isLocalhost()
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
                env.getProperty("api.openai.model.gpt.4.mini"),
                env.getProperty("api.openai.model.gpt.3.5"),
                env.getProperty("api.openai.model.gpt.3.5.turbo.instruct"));
        return openAiGptConfig;
    }


    @Bean
    public JavaMailSender javaMailSender() {
        if (Utils.isLocalhost()) {
            return javaMailSenderGmailDev();
        } else {
            return javaMailSenderLunaSapiensProd();
        }
    }

    private JavaMailSender javaMailSenderGmailDev() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setUsername(env.getProperty("gmail.mail.username"));
        List<String> loadPorpoerty = Utils.loadPropertiesEsternoLunaSapiens( new ArrayList<String>(Arrays.asList("gmail.mail.password")) );
        mailSender.setPassword( loadPorpoerty.get(0) );
        mailSender.setHost(env.getProperty("gmail.mail.smtp.host"));
        mailSender.setPort(Integer.parseInt(env.getProperty("gmail.mail.smtp.port")));

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



    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                //.expireAfterAccess(Duration.ofDays(1))
                .maximumSize(1000)); // massimo numero di elementi
        return cacheManager;
    }



    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }




    /**
     * Più recente adatto per le chiamate asincrone e in un ambiente reattivo in cui le operazioni non sono bloccanti
     *  È utilizzato per effettuare chiamate HTTP verso altre API web. Viene utilizzato per inviare richieste HTTP (GET, POST, PUT, DELETE) e gestire le risposte.
     * @return
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    /**
     *  È utilizzato per effettuare chiamate HTTP verso altre API web. Viene utilizzato per inviare richieste HTTP (GET, POST, PUT, DELETE) e gestire le risposte.
     * @return
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver(){
        // SpringResourceTemplateResolver si integra automaticamente con l'infrastruttura di risoluzione delle risorse di Spring stessa,
        // il che è altamente raccomandato.
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(this.applicationContext);
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        // HTML è il valore predefinito, aggiunto qui per chiarezza.
        templateResolver.setTemplateMode(TemplateMode.HTML);
        // La cache dei template è abilitata per impostazione predefinita. Impostare su false se si desidera
        // che i template vengano aggiornati automaticamente quando vengono modificati.
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(){
        // SpringTemplateEngine applica automaticamente SpringStandardDialect e
        // abilita i meccanismi di risoluzione dei messaggi di Spring attraverso MessageSource.
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();

        templateEngine.addDialect(new SpringSecurityDialect()); // Registrazione del dialetto di sicurezza

        templateEngine.setTemplateResolver(templateResolver());
        // Abilitare il compilatore SpringEL con Spring 4.2.4 o successivo può
        // accelerare l'esecuzione nella maggior parte dei casi, ma potrebbe essere incompatibile
        // con casi specifici in cui le espressioni in un template vengono riutilizzate
        // tra diversi tipi di dati, quindi questo flag è "false" per compatibilità all'indietro più sicura.
        templateEngine.setEnableSpringELCompiler(false);
        return templateEngine;
    }

    @Bean
    public ThymeleafViewResolver viewResolver(){
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        return viewResolver;
    }




    // ----------------------------------------------------

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

