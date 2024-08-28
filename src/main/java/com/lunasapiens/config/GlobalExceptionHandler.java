package com.lunasapiens.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;


/**
 * Qui si catturano le eccezzioni a livello applicativo, cioè nel contesto delle richieste HTTP convenzionali gestite dai controller Spring MVC
 *
 * Se l'eccezione viene sollevata durante la configurazione di una connessione WebSocket (per esempio in WebSocketConfig.java), che non segue lo stesso
 * ciclo di vita delle richieste HTTP, allora qui in @ControllerAdvice GlobalExceptionHandler non può essere catturata.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /* Esempio:
    @ExceptionHandler(EccezzioneCustom.class)
    public ModelAndView handleUserNotAuthenticatedException(UserNotAuthenticatedException ex) {
        logger.info("sono in EccezzioneCustom");
        ModelAndView mav = new ModelAndView("error");
        mav.setStatus(HttpStatus.FORBIDDEN); // O il codice di stato che preferisci
        mav.addObject(Constants.INFO_ERROR, ex.getMessage());
        return mav;
    }

     */



}
