package com.adityachandel.tracklore.service;

import com.adityachandel.tracklore.config.properties.MailProperties;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackloreEmailService {

    private final MailProperties mailProperties;
    private JavaMailSenderImpl mailSender;

    @PostConstruct
    public void init() {
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", String.valueOf(mailProperties.isAuth()));
        props.put("mail.smtp.starttls.enable", String.valueOf(mailProperties.isStarttls()));
        props.put("mail.smtp.connectiontimeout", "15000");
        props.put("mail.smtp.timeout", "15000");
    }

    public void sendReportEmail(String to, String subject, String bodyText) {
        Thread.startVirtualThread(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, false);
                String fromAddress = (mailProperties.getFrom() != null && !mailProperties.getFrom().isBlank()) ? mailProperties.getFrom() : mailProperties.getUsername();
                helper.setFrom(fromAddress);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(bodyText, false);
                mailSender.send(message);
                log.info("Email sent to {}", to);
            } catch (MessagingException e) {
                log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            }
        });
    }
}