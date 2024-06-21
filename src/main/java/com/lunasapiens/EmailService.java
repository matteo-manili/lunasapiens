package com.lunasapiens;

import com.lunasapiens.entity.EmailUtenti;
import com.lunasapiens.repository.EmailUtentiRepository;
import com.lunasapiens.service.EmailUtentiService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Date;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailUtentiService emailUtentiService;

    @Autowired
    private EmailUtentiRepository emailUtentiRepository;

    private String defaultFrom = "LunaSapiens <info@lunasapiens.com>"; // Imposta il mittente predefinito



    private static final String CAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    //private static final String SECRET_KEY = "6LeIif0pAAAAAPiK4zushl4ekl7mIBJgqM3-3QB1";

    private static final String SECRET_KEY = "6Lcwh_4pAAAAAA5rlg3jfOXe4qnDx1lPvdW2a7ag6Lcwh_4pAAAAAA5rlg3jfOXe4qnDx1lPvdW2a7ag";

// CHIAVE SEGRETA: 6Lcwh_4pAAAAAA5rlg3jfOXe4qnDx1lPvdW2a7ag6Lcwh_4pAAAAAA5rlg3jfOXe4qnDx1lPvdW2a7ag
// CHIAVE PUBBLICA: 6Lcwh_4pAAAAAPOqOANNuJV6qicy6iEAz641WXbO


    /**
     * DA QUI SCREANO LE CHIAVI: https://www.google.com/recaptcha/admin/create
     * DA QUI SI CONFIGURA: https://www.google.com/recaptcha/admin/site/704546608/setup
     * @param response
     * @return
     */
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



    public boolean salvaEmail(String email) {
        try{
            EmailUtenti emailUtenti = emailUtentiService.salvaEmailUtenti(email, new Date(), true);
            return true;
        } catch (DataIntegrityViolationException e) {
            System.out.println("Duplicate email detected: " + e.getMessage());
            // Puoi aggiungere altre azioni come logging o ripristino
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        return false;
    }


    public void sendEmailFromInfoLunaSapiens(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom( defaultFrom ); // Specifica il mittente desiderato
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }





}
