package com.example.taskmanagerlast;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    // Basit metin e-postası gönderme
    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom("noreply@taskmanager.com", "Task Manager Sistemi");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("E-posta gönderilemedi: " + e.getMessage());
        }
    }

    // HTML template ile e-posta gönderme
    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlContent = templateEngine.process(templateName, context);

            helper.setFrom("noreply@taskmanager.com", "Task Manager Sistemi");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("HTML e-postası gönderilemedi: " + e.getMessage());
        }
    }

    // Ekli dosya ile e-posta gönderme
    public void sendEmailWithAttachment(String to, String subject, String text,
                                        byte[] attachment, String filename) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("noreply@taskmanager.com", "Task Manager Sistemi");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            helper.addAttachment(filename, new ByteArrayResource(attachment));

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Eklenti gönderilemedi: " + e.getMessage());
        }
    }

    // Özel Exception Sınıfı
    private static class EmailException extends RuntimeException {
        public EmailException(String message) {
            super(message);
        }
    }
}