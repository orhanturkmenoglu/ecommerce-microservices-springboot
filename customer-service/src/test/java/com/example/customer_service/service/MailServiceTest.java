package com.example.customer_service.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private MailService mailService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendHtmlEmail_ShouldSendEmail_WhenValidRequest() throws Exception {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String customerName = "John Doe";
        String customerId = "12345";
        String customerEmail = "johndoe@example.com";

        // Thymeleaf şablonunun döndüreceği HTML içeriği
        String fakeHtmlContent = "<html><body><h1>Welcome, John Doe!</h1></body></html>";
        when(templateEngine.process(eq("create-customer"), any(Context.class))).thenReturn(fakeHtmlContent);

        // Act
        mailService.sendHtmlEmail(to, subject, customerName, customerId, customerEmail);

        // Assert
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
        verify(templateEngine, times(1)).process(eq("create-customer"), any(Context.class));
    }
}
