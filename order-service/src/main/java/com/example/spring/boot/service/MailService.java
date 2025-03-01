package com.example.spring.boot.service;

import com.example.spring.boot.dto.orderDto.OrderResponseDto;
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
    private final TemplateEngine templateEngine;

    // Basit bir e-posta gönderim metodu
    public void sendSimpleEmail(String to, String subject, String text) throws Exception {
      try {
          SimpleMailMessage message = new SimpleMailMessage();
          message.setTo(to);
          message.setSubject(subject);
          message.setText(text);
          javaMailSender.send(message);
          log.info("MailService::sendSimpleEmail  message : {}",message);
      } catch (Exception exception){
          throw  new Exception("Exception : "+exception.getMessage());
      }
    }

    // HTML içerikli e-posta gönderim metodu
    public void sendHtmlEmail(String to, String subject, OrderResponseDto orderResponseDto) throws Exception {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true = multipart

            // Thymeleaf ile HTML şablonunu render et
            Context context = new Context();
            context.setVariable("customerId", orderResponseDto.getCustomerId());
            context.setVariable("orderId", orderResponseDto.getId());
            context.setVariable("productId", orderResponseDto.getProductId());
            context.setVariable("quantity", orderResponseDto.getQuantity());
            context.setVariable("totalAmount", orderResponseDto.getTotalAmount());
            context.setVariable("orderStatus", orderResponseDto.getOrderStatus());
            context.setVariable("shippingAddress", orderResponseDto.getShippingAddress());

            String htmlContent = templateEngine.process("create-order", context);

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
