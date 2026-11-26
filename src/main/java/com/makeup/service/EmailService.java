package com.makeup.service;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.makeup.util.EmailSender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService implements EmailSender {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final JavaMailSender mailSender;
    private final String from;

    public EmailService(JavaMailSender mailSender,
            @Value("${spring.mail.username}") String mailUsername,
            @Value("${app.mail.from:}") String from) {
        this.mailSender = mailSender;
        this.from = (from == null || from.isBlank()) ? mailUsername : from;
    }

    public String generateCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    public void sendVerificationCode(String to, String username, String code) {
        String emailBody = "Bonjour " + "<h1>" + username + "</h1>"
                + " Votre code de validation est " + "<h1>" + code + "</h1>";
        sendEmail(to, emailBody);
    }

    @Override
    public void sendEmail(String toEmail, String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(body, true);
            helper.setTo(toEmail);
            helper.setSubject("Confirm your email");
            helper.setFrom(from);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email", e);
        } catch (RuntimeException e) {
            throw new IllegalStateException("failed to send email", e);
        }
    }
}
