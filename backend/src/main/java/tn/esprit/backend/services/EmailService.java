package tn.esprit.backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import tn.esprit.backend.exceptions.EmailSendingException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendCommentNotification(String to, String username, String postTitle, String commentContent, Integer likes, Integer views) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("New Comment on Your Post");

            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("postTitle", postTitle);
            context.setVariable("commentContent", commentContent);
            context.setVariable("likes", likes);
            context.setVariable("views", views);

            String htmlContent = templateEngine.process("email/comment-notification", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Email sent successfully to: " + to);
        } catch (MessagingException e) {
            System.err.println("Failed to send email to: " + to + ". Error: " + e.getMessage());
            // Log the error but don't throw an exception, so the notification endpoint doesn't fail
        } catch (Exception e) {
            System.err.println("Unexpected error while sending email to: " + to + ". Error: " + e.getMessage());
        }
    }
}