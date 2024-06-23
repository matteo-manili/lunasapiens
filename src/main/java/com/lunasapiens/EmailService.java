package com.lunasapiens;

import com.lunasapiens.entity.EmailUtenti;
import com.lunasapiens.repository.EmailUtentiRepository;
import com.lunasapiens.service.EmailUtentiService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    private EmailUtentiService emailUtentiService;

    @Autowired
    private EmailUtentiRepository emailUtentiRepository;

    @Autowired
    private TelegramBotClient telegramBotClient;

    private final String defaultFrom = "LunaSapiens <info@lunasapiens.com>"; // Imposta il mittente predefinito




    public void inviaConfermaEmailOrosciopoGioraliero(EmailUtenti emailUtenti) {
        EmailUtenti emailUtentiSetRandomCode = emailUtentiService.findByEmailUtenti( emailUtenti.getEmail() ).orElse(null);
        if( emailUtentiSetRandomCode != null ) {
            String confirmationCode = generateRandomCode();
            emailUtenti.setConfirmationCode(confirmationCode);
            emailUtentiRepository.save(emailUtenti);
            String linkConfirm = Constants.DOM_LUNA_SAPIENS + Constants.DOM_LUNA_SAPIENS_CONFIRM_EMAIL_OROSC_GIORN + "?code="+confirmationCode;
            String subject = "LunaSapiens - Conferma sottoscrizione Oroscopo del giorno";
            String text = "Grazie per esserti iscritto! Per confermare la tua iscrizione, clicca sul seguente link: " + linkConfirm;
            sendEmailFromInfoLunaSapiens(emailUtenti.getEmail(), subject, text);
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


    public void sendEmailFromInfoLunaSapiens(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        //message.setFrom(defaultFrom); // Specifica il mittente desiderato
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
