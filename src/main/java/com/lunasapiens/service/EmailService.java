package com.lunasapiens.service;

import com.lunasapiens.Constants;
import com.lunasapiens.Utils;
import com.lunasapiens.dto.ContactFormDTO;
import com.lunasapiens.dto.GiornoOraPosizioneDTO;
import com.lunasapiens.dto.OroscopoDelGiornoDescrizioneDTO;
import com.lunasapiens.dto.OroscopoGiornalieroDTO;
import com.lunasapiens.entity.ProfiloUtente;
import com.lunasapiens.entity.OroscopoGiornaliero;
import com.lunasapiens.repository.ProfiloUtenteRepository;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private ProfiloUtenteService profiloUtenteService;

    @Autowired
    private ProfiloUtenteRepository profiloUtenteRepository;

    @Autowired
    private TelegramBotService telegramBotService;

    @Autowired
    ServizioOroscopoDelGiorno servizioOroscopoDelGiorno;

    @Autowired
    OroscopoGiornalieroService oroscopoGiornalieroService;

    private final String defaultFromLunaSapiens = "LunaSapiens <info@lunasapiens.com>"; // Imposta il mittente predefinito
    private final String defaultFromGmailMatteoManili = "LunaSapiens <matteo.manili@gmail.com>"; // Imposta il mittente predefinito

    public static final String emailSubscription = "emailSubscription";
    public static final String emailOroscopo = "emailOroscopo";
    public static final String contenutoEmail = "contenutoEmail";




    // ###################### EMAIL REGISTRAZION PROFILO UTENTE ####################################

    public void inviaemailRegistrazioneUtente(ProfiloUtente profiloUtente, String codeTokenJwt) {
        if( profiloUtente != null ) {
            String subject = "LunaSapiens - Conferma iscrizione LunaSapiens";
            Context context = new Context();
            String linkConfirm = Constants.DOM_LUNA_SAPIENS +"/"+ "confirmRegistrazioneUtente" + "?code="+codeTokenJwt;
            String contenuto = "<b>Grazie per esserti iscritto a LunaSapiens.</b><br><br>" +
                    "Per autenticarti e accedere a LunsaSapiens, clicca sul seguente link <br>" + linkConfirm +
                    "<br><br>" + "<i>Se non hai mai visitato il sito LunaSapiens e hai ricevuto questa email per errore, puoi ignorarla.</i>";

            context.setVariable(contenutoEmail, contenuto);
            sendHtmlEmail(profiloUtente.getEmail(), subject, emailSubscription, context);
        }
    }



    // ###################### EMAIL TEMA NATALE ####################################

    public void inviaConfermaEmailTemaNatale(ProfiloUtente profiloUtente) {
        ProfiloUtente profiloUtenteSetRandomCode = profiloUtenteService.findByProfiloUtente( profiloUtente.getEmail() ).orElse(null);
        if( profiloUtenteSetRandomCode != null ) {
            String confirmationCode = UUID.randomUUID().toString();
            profiloUtente.setConfirmationCode(confirmationCode);
            profiloUtenteRepository.save(profiloUtente);

            String subject = "LunaSapiens - Conferma iscrizione aggiornamenti Tema Natale IA";
            Context context = new Context();
            String linkConfirm = Constants.DOM_LUNA_SAPIENS +"/"+ Constants.DOM_LUNA_SAPIENS_CONFIRM_EMAIL_TEMA_NATALE + "?code="+confirmationCode;
            String contenuto = "<b>Grazie per esserti iscritto per gli aggiornamenti del Tema Natale IA.</b><br><br>" +
                    "Per confermare l'iscrizione, clicca sul seguente link <br>" +
                    linkConfirm + "<br><br>" +
                    "<i>Se non hai mai visitato il sito LunaSapiens e hai ricevuto questa email per errore, puoi ignorarla.</i>";

            context.setVariable(contenutoEmail, contenuto);
            sendHtmlEmail(profiloUtente.getEmail(), subject, emailSubscription, context);
        }
    }



    // ###################### EMAIL OROSCOPO GIORNALIERO ####################################

    public void inviaConfermaEmailOrosciopoGioraliero(ProfiloUtente profiloUtente) {
        ProfiloUtente profiloUtenteSetRandomCode = profiloUtenteService.findByProfiloUtente( profiloUtente.getEmail() ).orElse(null);
        if( profiloUtenteSetRandomCode != null ) {
            String confirmationCode = UUID.randomUUID().toString();
            profiloUtente.setConfirmationCode(confirmationCode);
            profiloUtenteRepository.save(profiloUtente);

            String subject = "LunaSapiens - Conferma iscrizione Oroscopo del giorno";
            Context context = new Context();
            String linkConfirm = Constants.DOM_LUNA_SAPIENS +"/"+ Constants.DOM_LUNA_SAPIENS_CONFIRM_EMAIL_OROSC_GIORN + "?code="+confirmationCode;
            String contenuto = "<b>Grazie per esserti iscritto all'Oroscopo del giorno.</b><br><br>" +
                    "Per confermare l'iscrizione, clicca sul seguente link <br>" +
                    linkConfirm + "<br><br>" +
                    "<i>Se non hai mai visitato il sito LunaSapiens e hai ricevuto questa email per errore, puoi ignorarla.</i>";

            context.setVariable(contenutoEmail, contenuto);
            sendHtmlEmail(profiloUtente.getEmail(), subject, emailSubscription, context);
        }
    }

    /**
     * Limite invio email GoDaddy piano base
     * Gli account di hosting standard di GoDaddy sono limitati a 250 destinatari email al giorno.
     * 300 messaggi all'ora e 200 messaggi al minuto.
     * @return
     */
    public int inviaEmailOroscopoGioraliero() {
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = Utils.GiornoOraPosizione_OggiRomaOre12();
        OroscopoDelGiornoDescrizioneDTO oroscDelGiornDescDTO = servizioOroscopoDelGiorno.descrizioneOroscopoDelGiorno(giornoOraPosizioneDTO);
        List<OroscopoGiornaliero> listOroscopoGiorn = oroscopoGiornalieroService.findAllByDataOroscopoWithoutVideo(Utils.OggiRomaOre12());

        // 🧾 Converte la lista in DTO per usarla nelle email
        List<OroscopoGiornalieroDTO> listOroscopoGiornoDTO = listOroscopoGiorn.stream()
                .map(OroscopoGiornalieroDTO::new)
                .collect(Collectors.toList());

        // 👥 Recupera tutti gli utenti con oroscopo giornaliero attivo
        List<ProfiloUtente> utentiConOroscopoAttivoList = profiloUtenteService.getUtentiConOroscopoAttivo();

        // 🧵 CREA UN POOL DI THREAD per eseguire invii in parallelo ma limitati
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // 📦 Lista dei task asincroni (futures) per attendere che finiscano tutti
        List<Future<?>> futures = new ArrayList<>();

        // 🔢 Variabile sicura per conteggiare le email inviate (Atomic = thread-safe)
        AtomicInteger totaleNumEmailInviate = new AtomicInteger(0);

        // 🔁 Ciclo su tutti gli utenti
        for (ProfiloUtente emailUtente : utentiConOroscopoAttivoList) {
            // 🧵 INVIO ASINCRONO (nuova parte)
            // Invece di inviare l’email subito, lo affidiamo al pool di thread
            futures.add(executor.submit(() -> {
                try {
                    // 📨 Componi l’oggetto dell’email
                    String subject = "Orosocpo " + giornoOraPosizioneDTO.getGiornoMeseAnnoFormattato() + " - LunaSapiens";

                    // ✏️Prepara il contesto Thymeleaf con le variabili necessarie
                    Context context = new Context();
                    context.setVariable("oroscDelGiornDescDTO", oroscDelGiornDescDTO);
                    context.setVariable("listOroscopoGiornoDTO", listOroscopoGiornoDTO);
                    context.setVariable("confirmationCode", emailUtente.getConfirmationCode());

                    // Pausa 1 secondo per evitare limiti SMTP GoDaddy
                    Thread.sleep(1000);

                    // 📤 Invio dell’email
                    sendHtmlEmail(emailUtente.getEmail(), subject, emailOroscopo, context);

                    // 📈 Incrementa il conteggio email inviate (in modo sicuro)
                    totaleNumEmailInviate.incrementAndGet();

                    // 📝 Log positivo
                    logger.info("Email inviata a: {}", emailUtente.getEmail());

                } catch (Exception e) {
                    // ❌ Se qualcosa va storto, logga e avvisa su Telegram
                    logger.error("Errore invio email a {}: {}", emailUtente.getEmail(), e.getMessage(), e);
                    telegramBotService.inviaMessaggio("Errore invio email oroscopo: " + e.getMessage());
                }
            }));
        }

        // 🕒 ASPETTA CHE TUTTI I TASK FINISCANO (nuova parte)
        // Serve per assicurarci che tutte le email siano state inviate prima di chiudere
        for (Future<?> f : futures) {
            try {
                f.get(); // blocca finché il singolo invio è completato (oppure usa timeout se vuoi)
            } catch (Exception e) {
                logger.error("Errore durante l'attesa dei task: {}", e.getMessage(), e);
            }
        }

        // 🔚 Chiude il pool di thread in modo ordinato
        executor.shutdown();

        // 🔢 Ritorna il numero totale di email inviate correttamente
        return totaleNumEmailInviate.get();
    }


    public int inviaEmailOroscopoGioraliero_OLD() {
        GiornoOraPosizioneDTO giornoOraPosizioneDTO = Utils.GiornoOraPosizione_OggiRomaOre12();
        OroscopoDelGiornoDescrizioneDTO oroscDelGiornDescDTO = servizioOroscopoDelGiorno.descrizioneOroscopoDelGiorno(giornoOraPosizioneDTO);
        List<OroscopoGiornaliero> listOroscopoGiorn = oroscopoGiornalieroService.findAllByDataOroscopoWithoutVideo(Utils.OggiRomaOre12());
        List<ProfiloUtente> utentiConOroscopoAttivoList = profiloUtenteService.getUtentiConOroscopoAttivo();
        int totaleNumEmailInviate = 0;
        for(ProfiloUtente emailUtente: utentiConOroscopoAttivoList){
            try{
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
                totaleNumEmailInviate += 1;
                logger.info("inviaEmailOrosciopoGioraliero Email inviata a: {}", emailUtente.getEmail());

            } catch (Exception e) {
                logger.error("inviaEmailOroscopoGioraliero Exception per utente {}: {}", emailUtente.getEmail(), e.getMessage(), e);
                telegramBotService.inviaMessaggio("inviaEmailOroscopoGioraliero Exception: " + e.getMessage());
            }
        }
        return totaleNumEmailInviate;
    }


    public Object[] salvaEmail(String email, String ipAddress) {
        Object[] result = new Object[3];
        try{
            Optional<ProfiloUtente> profiloUtenteOptional = profiloUtenteRepository.findByEmail(email);
            if (profiloUtenteOptional.isPresent()) {
                ProfiloUtente profiloUtente = profiloUtenteOptional.get();
                profiloUtente.setDataUltimoAccesso( LocalDateTime.now() );
                profiloUtente.setEmailOroscopoGiornaliero(true);
                profiloUtente.setEmailAggiornamentiTemaNatale(true);
                profiloUtente.setConfirmationCode( UUID.randomUUID().toString() );
                profiloUtenteRepository.save(profiloUtente);
                result[0] = true; // Indica fallimento
                result[1] = "L'indirizzo email " + email + " è già iscritto nel sistema. Se non hai confermato l'iscrizione, controlla la tua casella di posta.";
                result[2] = profiloUtenteOptional.get();
            } else {

                ProfiloUtente newProfiloUtente = profiloUtenteService.salvaProfiloUtente( email, null, null, LocalDateTime.now(), null,
                        ipAddress, true, true, UUID.randomUUID().toString() );
                result[0] = true; // Indica successo
                result[1] = "Indirizzo email salvato con successo. Ti abbiamo inviato un'email di conferma all'indirizzo " + email + ". " +
                        "Controlla la tua casella di posta per confermare la tua iscrizione.";
                result[2] = newProfiloUtente;
            }
            telegramBotService.inviaMessaggio( "Email registrata: "+email);

        } catch (DataIntegrityViolationException e) {
            System.out.println("Duplicate email detected: " + e.getMessage());
            result[0] = false; // Indica fallimento
            result[1] = email + " errore duplicate email detected.";
            result[2] = null;

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            result[0] = false; // Indica fallimento
            result[1] = email + " errore salvataggio email.";
            result[2] = null;
        }
        return result;
    }


    public void inviaEmailContatti(ContactFormDTO contactForm) {
        String subject = "Luna Sapiens | messaggio inviato da: "+contactForm.getName() +" - "+contactForm.getEmail();
        Context context = new Context();
        String contenuto = "<p>"+subject+"</p>" + "<p>"+contactForm.getMessage()+"</p>";
        context.setVariable(contenutoEmail, contenuto);
        sendHtmlEmail(defaultFromGmailMatteoManili, subject, emailSubscription, context);
    }


    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(Utils.isLocalhost() ? defaultFromGmailMatteoManili : defaultFromLunaSapiens);
            helper.setTo(to);
            helper.setSubject(subject);
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Errore specifico (MessagingException) durante l'invio email a {}: {}", to, e.getMessage(), e);
            telegramBotService.inviaMessaggio("sendHtmlEmail MessagingException: "+e.getMessage());

        } catch (MailException e) {
            logger.error("Errore (MailException): {}", e.getMessage(), e);
            telegramBotService.inviaMessaggio("sendHtmlEmail MailException: "+e.getMessage());

        } catch (Exception e) {
            logger.error("Errore (Exception): {}", e.getMessage(), e);
            telegramBotService.inviaMessaggio("sendHtmlEmail Exception: "+e.getMessage());
        }
    }


    public void sendTextEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom( Utils.isLocalhost() ? defaultFromGmailMatteoManili : defaultFromLunaSapiens );
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
