package com.lunasapiens.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    private String defaultFrom = "LunaSapiens <info@lunasapiens.com>"; // Imposta il mittente predefinito



    public void sendEmailFromInfoLunaSapiens(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom( defaultFrom ); // Specifica il mittente desiderato
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }





}
