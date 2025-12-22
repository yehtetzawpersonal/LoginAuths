package com.loginAuth.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetEmail(String email, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText(
        	    "You requested to reset your password.\n\n"
        	  + "Click the link below to reset your password (valid for 3 minute):\n"
        	  + link + "\n\n"
        	  + "This link will expire in 3 minute.\n"
        	);
        mailSender.send(message);
    }
}
