package com.lunasapiens;

import com.lunasapiens.repository.OroscopoGiornalieroRepository;
import com.lunasapiens.service.EmailService;
import com.lunasapiens.service.TelegramBotService;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private ServizioOroscopoDelGiorno servizioOroscopoDelGiorno;

    @Autowired
    private OroscopoGiornalieroRepository oroscopoGiornalieroRepository;

    @Autowired
    private TelegramBotService telegramBotService;

    @Autowired
    private EmailService emailService;


    // (* secondi * minuti * ore * giorno del mese * mese * giorno della settimana)
    // settato per le 23:50 ogni giorno: "0 50 23 * * *"
    // settato per le 00:05 ogni giorno: "0 5 0 * * *"

    @Scheduled(cron = "0 3 0 * * *", zone = "Europe/Rome")
    public void executeTask_CreaOroscopoGiornaliero() {
        servizioOroscopoDelGiorno.creaOroscopoGiornaliero();
        telegramBotService.inviaMessaggio("executeTask Eseguito! ScheduledTasks.executeTask() "+ Utils.getNowRomeEurope());
        logger.info("executeTask_CreaOroscopoGiornaliero eseguito alle " + Utils.getNowRomeEurope());
    }

    @Scheduled(cron = "0 15 0 * * *", zone = "Europe/Rome")
    public void executeTask_PulisciTabellaOroscoGiornaliero() {
        Date currentDate_MenoUnGiorno = Date.from( Utils.getNowRomeEurope().minusDays(1).toInstant() ); // diminuisce di un giorno
        oroscopoGiornalieroRepository.deleteByDataOroscopoBefore(currentDate_MenoUnGiorno);
        logger.info("executeTask_PulisciTabellaOroscoGiornaliero");
    }

    @Scheduled(cron = "0 20 0 * * *", zone = "Europe/Rome")
    public void executeTask_InviaEmailOroscopoGioraliero() {
        int totaleNumEmailInviate = emailService.inviaEmailOroscopoGioraliero();
        telegramBotService.inviaMessaggio("totaleNumEmailInviate Oroscopo Giornaliero: "+totaleNumEmailInviate);
        logger.info("executeTask_InviaEmailOroscopoGioraliero eseguito alle " + Utils.getNowRomeEurope());
    }









}
