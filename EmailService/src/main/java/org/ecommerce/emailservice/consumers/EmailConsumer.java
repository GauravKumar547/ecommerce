package org.ecommerce.emailservice.consumers;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.ecommerce.emailservice.models.EmailLog;
import org.ecommerce.emailservice.models.EmailStatus;
import org.ecommerce.emailservice.repositories.EmailLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static org.ecommerce.emailservice.config.RabbitMQConfig.QUEUE_EMAIL;

@Component
public class EmailConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EmailConsumer.class);

    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailConsumer(JavaMailSender mailSender, EmailLogRepository emailLogRepository) {
        this.mailSender = mailSender;
        this.emailLogRepository = emailLogRepository;
    }

    @RabbitListener(queues = QUEUE_EMAIL)
    public void processEmail(EmailLog emailLog) {
        logger.info("Processing email: {}", emailLog.getMessageId());

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(emailLog.getRecipient());
            helper.setSubject(emailLog.getSubject());
            helper.setText(emailLog.getContent(), true);

            mailSender.send(message);

            emailLog.setStatus(EmailStatus.SENT);
            emailLog.setSentAt(LocalDateTime.now());
            logger.info("Email sent successfully: {}", emailLog.getMessageId());
        } catch (MessagingException e) {
            logger.error("Error sending email: {}", emailLog.getMessageId(), e);
            emailLog.setStatus(EmailStatus.FAILED);
            emailLog.setErrorMessage(e.getMessage());
        }

        emailLogRepository.save(emailLog);
    }
} 