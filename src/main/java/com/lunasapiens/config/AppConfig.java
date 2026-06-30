package com.lunasapiens.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.lunasapiens.Constants;
import com.lunasapiens.utils.Utils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.MultipartConfigElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.HiddenHttpMethodFilter;
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
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


@Configuration
@EnableCaching
public class AppConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    private final Environment environment;
    private final ApplicationContext applicationContext;

    public AppConfig(Environment environment, ApplicationContext applicationContext) {
        this.environment = environment;
        this.applicationContext = applicationContext;
    }

    public boolean isProduction(){
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

    public boolean isDevelopment(){
        return Arrays.asList(environment.getActiveProfiles()).contains("dev");
    }

    @PostConstruct
    public void printProfile() {
        logger.info("ACTIVE PROFILES: {}", (Object) environment.getActiveProfiles());
    }

    @Bean
    public HuggingFaceConfig huggingFaceConfig() {
        if (isDevelopment()) {
            List<String> loadPorpoerty = Utils.loadPropertiesEsternoLunaSapiens( new ArrayList<String>(Arrays.asList("hugging.face.name", "hugging.face.token")));
            return new HuggingFaceConfig(loadPorpoerty.get(0), loadPorpoerty.get(1));
        }else{
            return new HuggingFaceConfig(environment.getProperty("hugging.face.name"), environment.getProperty("hugging.face.token") );
        }
    }

    /**
     * Imposta la dimensione massima di un file caricato su 5 MB (per l'upload delle immagini in EditorArticleController)
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement("", 6 * 1024 * 1024, 6 * 1024 * 1024, 0); // 6MB
        return multipartConfigElement;
    }

    @Bean
    public JwtElements.JwtRsaKeys jwtRsaKeys() {
        if (isDevelopment()) {
            List<String> loadPorpoerty = Utils.loadPropertiesEsternoLunaSapiens( new ArrayList<String>(Arrays.asList("jwt.rsa.public.key", "jwt.rsa.private.key")));
            return new JwtElements.JwtRsaKeys(loadPorpoerty.get(0), loadPorpoerty.get(1));
        }else{
            return new JwtElements.JwtRsaKeys(environment.getProperty("jwt.rsa.public.key"), environment.getProperty("jwt.rsa.private.key") );
        }
    }

    @Bean
    public ApiGeonamesConfig getApiGeonames() {
        if (isDevelopment()) {
            List<String> loadPorpoerty = Utils.loadPropertiesEsternoLunaSapiens( new ArrayList<String>(Arrays.asList("api.geonames.username")) );
            return new ApiGeonamesConfig(loadPorpoerty.get(0)) ;
        }else{
            return new ApiGeonamesConfig( environment.getProperty("api.geonames.username") ) ;
        }
    }

    @Bean
    public GoogleRecaptchaConfig getRecaptchaKeys() {
        GoogleRecaptchaConfig googleRecaptchaConfig;
        if (isDevelopment()) {
            List<String> loadPorpoerty = Utils.loadPropertiesEsternoLunaSapiens( new ArrayList<String>(Arrays.asList(
                    "google.recaptcha.api-key", "google.recaptcha.site-key", "google.recaptcha.project-id")) );
            googleRecaptchaConfig = new GoogleRecaptchaConfig(loadPorpoerty.get(0), loadPorpoerty.get(1), loadPorpoerty.get(2));
        }else{
            googleRecaptchaConfig = new GoogleRecaptchaConfig( environment.getProperty("google.recaptcha.api-key"), environment.getProperty("google.recaptcha.site-key"), environment.getProperty("google.recaptcha.project-id") );
        }
        return googleRecaptchaConfig;
    }

    @Bean
    public S3ClientConfig s3ClientConfig() {
        if(isDevelopment()) {
            List<String> loadPorpoerty = Utils.loadPropertiesEsternoLunaSapiens( new ArrayList<String>(Arrays.asList(
                    "aws.access.key.id", "aws.secret.access.key", "aws.region", "aws.s3.bucket.name" )) );
            return new S3ClientConfig(loadPorpoerty.get(0), loadPorpoerty.get(1), loadPorpoerty.get(2), loadPorpoerty.get(3));
        }else{
            return new S3ClientConfig(environment.getProperty("aws.access.key.id"), environment.getProperty("aws.secret.access.key"),
                    environment.getProperty("aws.region"), environment.getProperty("aws.s3.bucket.name"));
        }
    }

    @Bean
    public FacebookConfig getfacebookConfig() {
        FacebookConfig facebookConfig;
        if (isDevelopment()) {
            List<String> loadPorpoerty = Utils.loadPropertiesEsternoLunaSapiens( new ArrayList<String>(Arrays.asList(
                    "api.facebook.appid", "api.facebook.appsecret", "api.facebook.idpage" )) );
            facebookConfig = new FacebookConfig(loadPorpoerty.get(0), loadPorpoerty.get(1), loadPorpoerty.get(2));
        }else{
            facebookConfig = new FacebookConfig( environment.getProperty("api.facebook.appid"), environment.getProperty("api.facebook.appsecret"), environment.getProperty("api.facebook.idpage") );
        }
        return facebookConfig;
    }

    @Bean
    public TelegramConfig getParamTelegram() {
        if (isDevelopment()) {
            List<String> loadPorpoerty = Utils.loadPropertiesEsternoLunaSapiens( new ArrayList<String>(Arrays.asList("api.telegram.token", "api.telegram.chatId",
                    "api.telegram.bot.username")) );
            return new TelegramConfig(loadPorpoerty.get(0), loadPorpoerty.get(1), loadPorpoerty.get(2));
        }else{
            return new TelegramConfig(environment.getProperty("api.telegram.token") , environment.getProperty("api.telegram.chatId"), environment.getProperty("api.telegram.bot.username"));
        }
    }

    @Bean
    public OpenAiGptConfig getParamOpenAi() {
        String apiKeyOpenAI = "";
        try{
            apiKeyOpenAI = environment.getProperty("api.key.openai");
            //System.out.println("111 api_openai: " + apiKeyOpenAI);
        } catch (IllegalArgumentException e) {
            // In caso di eccezione, utilizza il file di configurazione esterno. Prima che sviluppassio il metodo isLocalhost()
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream(Constants.FILE_CONFIG_ESTERNO)) {
                properties.load(fis);
                apiKeyOpenAI = properties.getProperty("api.key.openai");
                //System.out.println("222 api_openai: " + apiKeyOpenAI);
            } catch (IOException ioException) {
                throw new RuntimeException("Errore nella lettura del file di configurazione esterno.", ioException);
            }
        }
        OpenAiGptConfig openAiGptConfig = new OpenAiGptConfig(apiKeyOpenAI,
                environment.getProperty("api.openai.model.gpt.4"),
                environment.getProperty("api.openai.model.gpt.4.mini"),
                environment.getProperty("api.openai.model.gpt.3.5"),
                environment.getProperty("api.openai.model.gpt.3.5.turbo.instruct"));
        return openAiGptConfig;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        if (isDevelopment()) {
            return javaMailSenderGmailDev();
        } else {
            return javaMailSenderLunaSapiensProd();
        }
    }

    private JavaMailSender javaMailSenderGmailDev() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setUsername(environment.getProperty("gmail.mail.username"));
        List<String> loadPorpoerty = Utils.loadPropertiesEsternoLunaSapiens( new ArrayList<String>(Arrays.asList("gmail.mail.password")) );
        mailSender.setPassword( loadPorpoerty.get(0) );
        mailSender.setHost(environment.getProperty("gmail.mail.smtp.host"));
        mailSender.setPort(Integer.parseInt(environment.getProperty("gmail.mail.smtp.port")));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", environment.getProperty("mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", environment.getProperty("mail.smtp.starttls.enable", "true"));
        props.put("mail.debug", environment.getProperty("mail.debug", "false"));

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
        mailSender.setUsername(environment.getProperty("mail.username"));
        mailSender.setPassword(environment.getProperty("mail.password"));
        mailSender.setHost(environment.getProperty("mail.smtp.host"));
        mailSender.setPort(Integer.parseInt(environment.getProperty("mail.smtp.port")));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", environment.getProperty("mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", environment.getProperty("mail.smtp.starttls.enable", "true"));
        props.put("mail.debug", environment.getProperty("mail.debug", "false"));

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
        templateResolver.setCharacterEncoding("UTF-8");
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

    /**
     * Configura un filtro per abilitare i metodi HTTP non standard (PUT, DELETE) nei form HTML.
     * Il filtro intercetta le richieste HTTP e, se trova un parametro `_method`, ne modifica il
     * metodo (ad esempio, da POST a DELETE o PUT). Questo consente di utilizzare operazioni
     * RESTful nei form tradizionali, che supportano solo GET e POST.
     *
     * Esempio:
     * <input type="hidden" name="_method" value="DELETE" />
     *
     * @return un'istanza di HiddenHttpMethodFilter.
     */
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


    /*
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        try{
            dataSource.setUrl(environment.getProperty("spring.datasource.url"));
            dataSource.setUsername(environment.getProperty("spring.datasource.username"));
            dataSource.setPassword(environment.getProperty("spring.datasource.password"));
        } catch (IllegalArgumentException e) {
            // In caso di eccezione, utilizza il file di configurazione esterno
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream(Constants.FILE_CONFIG_ESTERNO)) {
                properties.load(fis);
                dataSource.setUrl(properties.getProperty("spring.datasource.url"));
                dataSource.setUsername(properties.getProperty("spring.datasource.username"));
                dataSource.setPassword(properties.getProperty("spring.datasource.password"));
            } catch (IOException ioException) {
                throw new RuntimeException("Errore nella lettura del file di configurazione esterno.", ioException);
            }
        }
        logger.info("getPostgreSQLVersion: "+getPostgreSQLVersion(dataSource));
        return dataSource;
    }
    */


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        if (isProduction()) {
            dataSource.setUrl(environment.getProperty("spring.datasource.url"));
            dataSource.setUsername(environment.getProperty("spring.datasource.username"));
            dataSource.setPassword(environment.getProperty("spring.datasource.password"));
        } else {
            // In caso di eccezione, utilizza il file di configurazione esterno
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream(Constants.FILE_CONFIG_ESTERNO)) {
                properties.load(fis);
                dataSource.setUrl(properties.getProperty("spring.datasource.url"));
                dataSource.setUsername(properties.getProperty("spring.datasource.username"));
                dataSource.setPassword(properties.getProperty("spring.datasource.password"));
            } catch (IOException ioException) {
                throw new RuntimeException("Errore nella lettura del file di configurazione esterno.", ioException);
            }
        }
        logger.info("getPostgreSQLVersion: "+getPostgreSQLVersion(dataSource));
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
        /*
        registry
                .addResourceHandler("/static/oroscopo_giornaliero/**") // Percorso URL delle risorse statiche
                .addResourceLocations("classpath:/static/oroscopo_giornaliero/", "file:src/main/resources/static/oroscopo_giornaliero/")
                .setCachePeriod(0);
         */
    }


}

