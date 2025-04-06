package esprit.microproject.Services;

// ... (other imports: Order, MessagingException, MimeMessage, Logger, Autowired, Value, ByteArrayResource, MailException, JavaMailSender, MimeMessageHelper, Async, Service, Context, SpringTemplateEngine)
import esprit.microproject.Entities.Order;
import esprit.microproject.Entities.User; // Import User
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private PdfService pdfService;

    @Value("${spring.mail.properties.mail.from:noreply@example.com}")
    private String fromAddress;

    @Async
    public void sendOrderConfirmationEmail(Order order) {
        User user = order.getUser(); // Get the user from the order

        // **** IMPORTANT: Check for user and email ****
        if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
            logger.error("Cannot send email: User or user email is missing/blank for Order ID {}", order.getId());
            // Optionally, notify an admin or log differently
            return; // Stop processing
        }
        // *********************************************

        String recipientEmail = user.getEmail(); // Use the actual user's email
        String subject = "Your Order #" + order.getId() + " Confirmation";

        try {
            byte[] pdfBytes = pdfService.generateInvoicePdf(order);
            if (pdfBytes == null) {
                logger.error("Skipping email for Order ID {}: PDF generation failed.", order.getId());
                return;
            }

            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("order", order);
            templateModel.put("username", user.getUsername()); // Pass username to template

            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(templateModel);
            String htmlBody = templateEngine.process("email/order-confirmation", thymeleafContext);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setFrom(fromAddress);
            helper.setText(htmlBody, true);
            helper.addAttachment("Invoice-" + order.getId() + ".pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(mimeMessage);
            logger.info("Order confirmation email sent successfully to {} for Order ID {}", recipientEmail, order.getId());

        } catch (MailException | MessagingException e) {
            logger.error("Failed to send order confirmation email for Order ID {}: {}", order.getId(), e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during email/PDF processing for Order ID {}: {}", order.getId(), e.getMessage(), e);
        }
    }
}