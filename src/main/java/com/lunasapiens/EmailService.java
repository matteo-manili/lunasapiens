package com.lunasapiens;

import com.lunasapiens.entity.EmailUtenti;
import com.lunasapiens.repository.EmailUtentiRepository;
import com.lunasapiens.service.EmailUtentiService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Service
public class EmailService {

    @Autowired
    private Environment env;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private EmailUtentiService emailUtentiService;

    @Autowired
    private EmailUtentiRepository emailUtentiRepository;

    @Autowired
    private TelegramBotClient telegramBotClient;

    private final String defaultFromLunaSapiens = "LunaSapiens <info@lunasapiens.com>"; // Imposta il mittente predefinito
    private final String defaultFromGmailMatteoManili = "matteo.manili@gmail.com"; // Imposta il mittente predefinito

    public static final String templateEmailFragment = "templateEmailFragment";
    public static final String contenutoEmail = "contenutoEmail";




    public void inviaConfermaEmailOrosciopoGioraliero(EmailUtenti emailUtenti) {
        EmailUtenti emailUtentiSetRandomCode = emailUtentiService.findByEmailUtenti( emailUtenti.getEmail() ).orElse(null);
        if( emailUtentiSetRandomCode != null ) {
            String confirmationCode = generateRandomCode();
            emailUtenti.setConfirmationCode(confirmationCode);
            emailUtentiRepository.save(emailUtenti);

            String subject = "LunaSapiens - Conferma iscrizione Oroscopo del giorno";
            Context context = new Context();
            String linkConfirm = Constants.DOM_LUNA_SAPIENS + Constants.DOM_LUNA_SAPIENS_CONFIRM_EMAIL_OROSC_GIORN + "?code="+confirmationCode;
            String contenuto = "<b>Grazie per esserti iscritto all'Oroscopo del giorno.</b><br><br>" +
                    "Per confermare la tua iscrizione, clicca sul seguente link <br>" +
                    linkConfirm + "<br><br>" +
                    "<i>Se non hai mai visitato il sito LunaSapiens e hai ricevuto questa email per errore, puoi ignorarla.</i>";

            context.setVariable(contenutoEmail, contenuto);
            sendHtmlEmail(emailUtenti.getEmail(), subject, templateEmailFragment, context);
        }
    }


    public void inviaEmailOrosciopoGioraliero(EmailUtenti emailUtenti) {
        EmailUtenti emailUtentiSetRandomCode = emailUtentiService.findByEmailUtenti( emailUtenti.getEmail() ).orElse(null);
        if( emailUtentiSetRandomCode != null ) {

            String subject = "LunaSapiens - Orosocpo del giorno";
            Context context = new Context();
            String linkCencelIscrizione = Constants.DOM_LUNA_SAPIENS + Constants.DOM_LUNA_SAPIENS_CANCELLA_ISCRIZ_OROSC_GIORN + "?code="+emailUtentiSetRandomCode.getConfirmationCode();
            String contenuto = "<h2>Oroscopo del Giorno</h2><p>Oggi è una giornata ideale per concentrarti sui tuoi obiettivi. <<br><strong>Lavoro:</strong> " +
                    "Le stelle ti favoriscono nelle decisioni lavorative. <br><strong>Amore:</strong> Mostra il tuo lato più affettuoso. " +
                    "<br><strong>Benessere:</strong> Ricorda di prenderti del tempo per te stesso. <br>Fortuna e serenità sono in arrivo!</p>";

            contenuto += "<p><i>Se desideri cancellarti dall'Oroscopo del giorno, puoi farlo cliccando sul seguente link " + linkCencelIscrizione + "</i></p>";

            context.setVariable(contenutoEmail, contenuto);
            //sendHtmlEmail(emailUtenti.getEmail(), subject, templateEmailFragment, context);

            sendHtmlEmail(emailUtenti.getEmail(), subject, templateEmailFragment, context);
        }
    }



    public static String generateRandomCode() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }


    public Object[] salvaEmail(String email) {
        telegramBotClient.inviaMessaggio( "Email registrata: "+email);
        Object[] result = new Object[3];
        try{
            EmailUtenti emailUtenti = emailUtentiService.salvaEmailUtenti(email, new Date(), false);
            result[0] = true; // Indica successo
            result[1] = "Indirizzo email salvato con successo. Ti abbiamo inviato un'email di conferma all'indirizzo " + email + ". " +
                    "Controlla la tua casella di posta per confermare la tua iscrizione.";
            result[2] = emailUtenti;

        } catch (DataIntegrityViolationException e) {
            System.out.println("Duplicate email detected: " + e.getMessage());
            result[0] = true; // Indica fallimento
            result[1] = "L'indirizzo email " + email + " è già registrato nel sistema. Se non hai confermato l'iscrizione, controlla la tua casella di posta.";
            result[2] = emailUtentiService.findByEmailUtenti( email ).orElse(null);

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            result[0] = false; // Indica fallimento
            result[1] = email + " errore salvataggio email.";
            result[2] = null;
        }
        return result;
    }





    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(appConfig.isLocalhost() ? defaultFromGmailMatteoManili : defaultFromLunaSapiens);
            helper.setTo(to);
            helper.setSubject(subject);

            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Gestisci l'eccezione in base alle tue necessità
        }
    }

    // da errore in produzione, manca anche il setFrom
    @Deprecated
    public void sendHtmlEmail_OLD(String to, String subject, String templateName, Context context) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            // Process the Thymeleaf template to get the HTML content
            String htmlContent = templateEngine.process(templateName, context);

            // Set email properties
            helper.setTo(to);
            helper.setSubject(subject);

            // Set the HTML content directly without additional encoding
            helper.setText(htmlContent, true); // true indicates HTML content

            // Send the email
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace(); // Handle exception properly
        }
    }




    public void sendTextEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom( appConfig.isLocalhost() ? defaultFromGmailMatteoManili : defaultFromLunaSapiens );
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }



    // -------- GOOGLE RECAPTCHA - NON LO USO ------------
    private static final String CAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final String SECRET_KEY = "6Lcwh_4pAAAAAA5rlg3jfOXe4qnDx1lPvdW2a7ag6Lcwh_4pAAAAAA5rlg3jfOXe4qnDx1lPvdW2a7ag";

    // CHIAVE SEGRETA: 6Lcwh_4pAAAAAA5rlg3jfOXe4qnDx1lPvdW2a7ag6Lcwh_4pAAAAAA5rlg3jfOXe4qnDx1lPvdW2a7ag
    // CHIAVE PUBBLICA: 6Lcwh_4pAAAAAPOqOANNuJV6qicy6iEAz641WXbO

    /** NON LO USO!!
     * Name LunaSapiens4
     * DA QUI SCREANO LE CHIAVI: https://www.google.com/recaptcha/admin/create
     * DA QUI SI CONFIGURA: https://www.google.com/recaptcha/admin/site/704546608/setup
     * @param response
     * @return
     */
    @Deprecated
    public synchronized boolean isCaptchaValid(String response) {
        try {
            String url = CAPTCHA_VERIFY_URL, params = "secret=" + SECRET_KEY + "&response=" + response;

            HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            OutputStream out = http.getOutputStream();
            out.write(params.getBytes("UTF-8"));
            out.flush();
            out.close();

            InputStream res = http.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(res, "UTF-8"));

            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }

            JSONObject json = new JSONObject(sb.toString());
            res.close();
            return json.getBoolean("success");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
