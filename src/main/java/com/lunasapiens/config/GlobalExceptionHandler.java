package com.lunasapiens.config;

import com.lunasapiens.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


//@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * TODO testare non l'ho mai provato questo errore e se lo chiama
     * @param ex
     * @param redirectAttributes
     * @return
     */

    //@ExceptionHandler(Exception.class)
    //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleGlobalException(Exception ex, RedirectAttributes redirectAttributes) {

        logger.info("sono in GlobalExceptionHandler handleGlobalException cause: "+ex.getCause());
        logger.info("sono in GlobalExceptionHandler handleGlobalException message: "+ex.getMessage());

        logger.info("sono in GlobalExceptionHandler handleGlobalException");



    }



}
