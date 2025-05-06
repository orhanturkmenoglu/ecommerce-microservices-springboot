package com.example.auth_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendEmailVerification(String to, String code) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Email Verification");
        message.setText("Your verification code is " + code);
        javaMailSender.send(message);

        log.info("Email sent to {}", to);
        log.info("Verification code: {}", code);
        log.info("Email sent successfully");
        log.info(String.format("Email SimpleMailMessage : %s", message));

    }
}
