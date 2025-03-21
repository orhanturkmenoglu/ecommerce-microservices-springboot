package com.example.customer_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine; // Thymeleaf template engine


    // HTML içerikli e-posta gönderim metodu
    public void sendHtmlEmail(String to, String subject, String customerName, String customerId, String customerEmail) throws Exception {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true = multipart

            // Thymeleaf ile HTML şablonunu render et
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("customerId", customerId);
            context.setVariable("customerEmail", customerEmail);

            String htmlContent = templateEngine.process("create-customer", context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML içeriği olarak gönder

            javaMailSender.send(message);
            log.info("MailService::sendHtmlEmail message sent to: {}", to);
        } catch (MessagingException messagingException) {
            throw new Exception("MessagingException : " + messagingException.getMessage());
        } catch (Exception exception) {
            throw new Exception("Exception : " + exception.getMessage());
        }
    }
}
