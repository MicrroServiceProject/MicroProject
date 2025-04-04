package esprit.projet_web.Service;

import esprit.projet_web.Entity.Reservation;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Envoi d'email texte simple
     */
    public void sendAdminNotification(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            logger.info("Email texte envoyé à {}", to);
        } catch (Exception e) {
            logger.error("Échec d'envoi d'email à {}", to, e);
            throw new EmailException("Échec d'envoi d'email", e);
        }
    }

    /**
     * Envoi d'email HTML avec template Thymeleaf
     */
    public void sendHtmlAdminNotification(String to, String subject, Reservation reservation) {
        try {
            Context context = new Context();
            context.setVariable("reservationId", reservation.getId());
            context.setVariable("clientName", reservation.getClient().getPrenom() + " " + reservation.getClient().getNom());
            context.setVariable("clientEmail", reservation.getClient().getEmail());
            context.setVariable("eventName", reservation.getEvenement().getNom());
            context.setVariable("places", reservation.getNbrPlace());
            context.setVariable("reservationDate", reservation.getDateReservation());

            String htmlContent = templateEngine.process("email-reservation", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indique que c'est du HTML
            mailSender.send(message);

            logger.info("Email HTML envoyé à {}", to);
        } catch (MessagingException e) {
            logger.error("Erreur de messagerie pour l'email à {}", to, e);
            throw new EmailException("Erreur technique lors de l'envoi d'email", e);
        }
    }

    // Exception personnalisée pour les erreurs d'email
    public static class EmailException extends RuntimeException {
        public EmailException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}