package com.lunasapiens;

import com.lunasapiens.repository.DatabaseMaintenanceRepository;
import com.lunasapiens.service.EmailService;
import com.lunasapiens.service.S3Service;
import com.lunasapiens.service.TelegramBotService;
import com.lunasapiens.zodiac.ServizioOroscopoDelGiorno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private ServizioOroscopoDelGiorno servizioOroscopoDelGiorno;

    @Autowired
    private DatabaseMaintenanceRepository databaseMaintenanceRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private TelegramBotService telegramBotService;

    @Autowired
    private EmailService emailService;


    // (* secondi * minuti * ore * giorno del mese * mese * giorno della settimana)
    // settato per le 23:50 ogni giorno: "0 50 23 * * *"
    // settato per le 00:05 ogni giorno: "0 5 0 * * *"

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Rome")
    public void executeTask_eliminaImmaginiArticoloNonUtilizzateBucketS3() {
        if(Utils.isLocalhost() == false) {
            s3Service.eliminaImmaginiArticoloNonUtilizzateBucketS3();
            logger.info("executeTask_eliminaImmaginiArticoloNonUtilizzateBucketS3");
        }
    }

    @Scheduled(cron = "0 3 0 * * *", zone = "Europe/Rome")
    public void executeTask_CreaOroscopoGiornaliero() {
        if(Utils.isLocalhost() == false) {
            servizioOroscopoDelGiorno.creaOroscopoGiornaliero(); // per fare questo processo ci mette circa 5 minuti (su server)
            telegramBotService.inviaMessaggio("executeTask Eseguito! ScheduledTasks.executeTask() " + Utils.getNowRomeEurope());
            logger.info("executeTask_CreaOroscopoGiornaliero eseguito alle " + Utils.getNowRomeEurope());
        }
    }

    @Scheduled(cron = "0 15 0 * * *", zone = "Europe/Rome")
    public void executeTask_PulisciOldRecordsOroscopoGiornaliero() {
        if(Utils.isLocalhost() == false) {
            databaseMaintenanceRepository.deleteOldOroscopoRecords();
            databaseMaintenanceRepository.vacuum();
            logger.info("executeTask_PulisciOldRecordsOroscopoGiornaliero");
        }
    }

    @Scheduled(cron = "0 20 0 * * *", zone = "Europe/Rome")
    public void executeTask_InviaEmailOroscopoGioraliero() {
        if(Utils.isLocalhost() == false){
            int totaleNumEmailInviate = emailService.inviaEmailOroscopoGioraliero();
            telegramBotService.inviaMessaggio("totaleNumEmailInviate Oroscopo Giornaliero: "+totaleNumEmailInviate);
            logger.info("executeTask_InviaEmailOroscopoGioraliero eseguito alle " + Utils.getNowRomeEurope());
        }
    }









}
