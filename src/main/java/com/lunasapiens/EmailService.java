package com.lunasapiens;

import com.lunasapiens.controller.IndexController;
import com.lunasapiens.dto.ContactFormDTO;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.dto.OroscopoDelGiornoDescrizioneDTO;
import com.lunasapiens.dto.OroscopoGiornalieroDTO;
import com.lunasapiens.entity.EmailUtenti;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.repository.EmailUtentiRepository;
import com.lunasapiens.service.EmailUtentiService;
import com.lunasapiens.service.OroscopoGiornalieroService;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

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

    @Autowired
    ServizioOroscopoDelGiorno servizioOroscopoDelGiorno;

    @Autowired
    OroscopoGiornalieroService oroscopoGiornalieroService;

    private final String defaultFromLunaSapiens = "LunaSapiens <info@lunasapiens.com>"; // Imposta il mittente predefinito
    private final String defaultFromGmailMatteoManili = "matteo.manili@gmail.com"; // Imposta il mittente predefinito

    public static final String emailSubscription = "emailSubscription";
    public static final String emailOroscopo = "emailOroscopo";
    public static final String contenutoEmail = "contenutoEmail";

    // ###################### EMAIL TEMA NATALE ####################################

    public void inviaConfermaEmailTemaNatale(EmailUtenti emailUtenti) {
        EmailUtenti emailUtentiSetRandomCode = emailUtentiService.findByEmailUtenti( emailUtenti.getEmail() ).orElse(null);
        if( emailUtentiSetRandomCode != null ) {
            String confirmationCode = generateRandomCode();
            emailUtenti.setConfirmationCode(confirmationCode);
            emailUtentiRepository.save(emailUtenti);

            String subject = "LunaSapiens - Conferma iscrizione aggiornamenti Tema Natale IA";
            Context context = new Context();
            String linkConfirm = Constants.DOM_LUNA_SAPIENS +"/"+ Constants.DOM_LUNA_SAPIENS_CONFIRM_EMAIL_TEMA_NATALE + "?code="+confirmationCode;
            String contenuto = "<b>Grazie per esserti iscritto per gli aggiornamenti del Tema Natale IA.</b><br><br>" +
                    "Per confermare l'iscrizione, clicca sul seguente link <br>" +
                    linkConfirm + "<br><br>" +
                    "<i>Se non hai mai visitato il sito LunaSapiens e hai ricevuto questa email per errore, puoi ignorarla.</i>";

            context.setVariable(contenutoEmail, contenuto);
            sendHtmlEmail(emailUtenti.getEmail(), subject, emailSubscription, context);
        }
    }



    // ###################### EMAIL OROSCOPO GIORNALIERO ####################################

    public void inviaConfermaEmailOrosciopoGioraliero(EmailUtenti emailUtenti) {
        EmailUtenti emailUtentiSetRandomCode = emailUtentiService.findByEmailUtenti( emailUtenti.getEmail() ).orElse(null);
        if( emailUtentiSetRandomCode != null ) {
            String confirmationCode = generateRandomCode();
            emailUtenti.setConfirmationCode(confirmationCode);
            emailUtentiRepository.save(emailUtenti);

            String subject = "LunaSapiens - Conferma iscrizione Oroscopo del giorno";
            Context context = new Context();
            String linkConfirm = Constants.DOM_LUNA_SAPIENS +"/"+ Constants.DOM_LUNA_SAPIENS_CONFIRM_EMAIL_OROSC_GIORN + "?code="+confirmationCode;
            String contenuto = "<b>Grazie per esserti iscritto all'Oroscopo del giorno.</b><br><br>" +
                    "Per confermare l'iscrizione, clicca sul seguente link <br>" +
                    linkConfirm + "<br><br>" +
                    "<i>Se non hai mai visitato il sito LunaSapiens e hai ricevuto questa email per errore, puoi ignorarla.</i>";

            context.setVariable(contenutoEmail, contenuto);
            sendHtmlEmail(emailUtenti.getEmail(), subject, emailSubscription, context);
        }
    }




    public void inviaEmailOrosciopoGioraliero() {
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = Util.GiornoOraPosizione_OggiRomaOre12();
        OroscopoDelGiornoDescrizioneDTO oroscDelGiornDescDTO = servizioOroscopoDelGiorno.oroscopoDelGiornoDescrizioneOggi(giornoOraPosizioneDTO);
        List<OroscopoGiornaliero> listOroscopoGiorn = oroscopoGiornalieroService.findAllByDataOroscopoWithoutVideo(Util.OggiOre12());
        List<EmailUtenti> emailUtentiList = emailUtentiService.findAll();
        for(EmailUtenti emailUtente: emailUtentiList){
            if( emailUtente.isSubscription() ){
                String subject = "Orosocpo "+giornoOraPosizioneDTO.getGiornoMeseAnnoFormattato() +" - LunaSapiens";
                Context context = new Context();
                List<OroscopoGiornalieroDTO> listOroscopoGiornoDTO = new ArrayList<>();
                for(OroscopoGiornaliero oroscopo : listOroscopoGiorn) {
                    OroscopoGiornalieroDTO dto = new OroscopoGiornalieroDTO(oroscopo);
                    listOroscopoGiornoDTO.add(dto);
                }
                context.setVariable("oroscDelGiornDescDTO", oroscDelGiornDescDTO);
                context.setVariable("listOroscopoGiornoDTO", listOroscopoGiornoDTO);
                context.setVariable("confirmationCode", emailUtente.getConfirmationCode());

                sendHtmlEmail(emailUtente.getEmail(), subject, emailOroscopo, context);
                logger.info("inviaEmailOrosciopoGioraliero: "+emailUtente.getEmail());
            }

        }


}



    public String generateRandomCode() {
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



    public void inviaEmailContatti(ContactFormDTO contactForm) {
        String subject = "LunaSapiens | messaggio inviato da: "+contactForm.getName() +" - "+contactForm.getEmail();
        Context context = new Context();
        String contenuto = "<p>"+subject+"</p>" + "<p>"+contactForm.getMessage()+"</p>";
        context.setVariable(contenutoEmail, contenuto);
        sendHtmlEmail(defaultFromGmailMatteoManili, subject, emailSubscription, context);
    }




    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(Util.isLocalhost() ? defaultFromGmailMatteoManili : defaultFromLunaSapiens);
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

    public void sendTextEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom( Util.isLocalhost() ? defaultFromGmailMatteoManili : defaultFromLunaSapiens );
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
