package org.ecommerce.emailservice.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.ecommerce.emailservice.dtos.EmailRequest;
import org.ecommerce.emailservice.dtos.EmailResponse;
import org.ecommerce.emailservice.models.EmailLog;
import org.ecommerce.emailservice.models.EmailStatus;
import org.ecommerce.emailservice.models.EmailTemplate;
import org.ecommerce.emailservice.repositories.EmailLogRepository;
import org.ecommerce.emailservice.services.EmailService;
import org.ecommerce.emailservice.services.EmailTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.ecommerce.emailservice.config.RabbitMQConfig.EXCHANGE_EMAIL;
import static org.ecommerce.emailservice.config.RabbitMQConfig.ROUTING_KEY_EMAIL;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private static final int MAX_RETRY_COUNT = 3;
    private static final int RETRY_DELAY_MINUTES = 15;

    private final JavaMailSender mailSender;
    private final RabbitTemplate rabbitTemplate;
    private final EmailTemplateService templateService;
    private final EmailLogRepository emailLogRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender,
                          RabbitTemplate rabbitTemplate,
                          EmailTemplateService templateService,
                          EmailLogRepository emailLogRepository) {
        this.mailSender = mailSender;
        this.rabbitTemplate = rabbitTemplate;
        this.templateService = templateService;
        this.emailLogRepository = emailLogRepository;
    }

    @Override
    @Transactional
    public EmailResponse sendEmail(EmailRequest request) {
        EmailLog emailLog = new EmailLog();
        emailLog.setRecipient(request.getRecipient());
        emailLog.setTemplateName(request.getTemplateName());
        emailLog.setMessageId(request.getMessageId());
        emailLog.setStatus(EmailStatus.QUEUED);

        try {
            EmailTemplate template = templateService.getTemplateByNameAndLanguage(
                    request.getTemplateName(), request.getLanguage());
            
            String processedContent = templateService.processTemplate(
                    request.getTemplateName(), request.getLanguage(), request.getTemplateData());
            
            emailLog.setSubject(template.getSubject());
            emailLog.setContent(processedContent);
            emailLog = emailLogRepository.save(emailLog);

            rabbitTemplate.convertAndSend(EXCHANGE_EMAIL, ROUTING_KEY_EMAIL, emailLog);
            
            return createEmailResponse(emailLog);
        } catch (Exception e) {
            logger.error("Error queuing email", e);
            emailLog.setStatus(EmailStatus.FAILED);
            emailLog.setErrorMessage(e.getMessage());
            emailLog = emailLogRepository.save(emailLog);
            return createEmailResponse(emailLog);
        }
    }

    @Override
    public EmailResponse getEmailStatus(Long id) {
        EmailLog emailLog = emailLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Email log not found with id: " + id));
        return createEmailResponse(emailLog);
    }

    @Override
    public Page<EmailResponse> getEmailsByRecipient(String recipient, Pageable pageable) {
        return emailLogRepository.findByRecipient(recipient, pageable)
                .map(this::createEmailResponse);
    }

    @Override
    @Scheduled(fixedDelay = 300000) // 5 minutes
    public void retryFailedEmails() {
        List<EmailLog> failedEmails = emailLogRepository.findByStatusAndRetryCountLessThan(
                EmailStatus.FAILED, MAX_RETRY_COUNT);

        for (EmailLog emailLog : failedEmails) {
            if (shouldRetry(emailLog)) {
                retryEmail(emailLog);
            }
        }
    }

    @Override
    @Scheduled(fixedDelay = 900000) // 15 minutes
    public void processDeadLetterQueue() {
        List<EmailLog> deadLetters = emailLogRepository.findByStatus(EmailStatus.DEAD_LETTER);
        
        for (EmailLog emailLog : deadLetters) {
            try {
                sendEmailDirectly(emailLog);
                emailLog.setStatus(EmailStatus.SENT);
                emailLog.setSentAt(LocalDateTime.now());
            } catch (Exception e) {
                logger.error("Error processing dead letter", e);
                emailLog.setErrorMessage(e.getMessage());
            }
            emailLogRepository.save(emailLog);
        }
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *") // Every day at midnight
    public void cleanupOldEmails(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        List<EmailLog> oldEmails = emailLogRepository.findByStatusAndLastRetryAtBefore(
                EmailStatus.SENT, cutoffDate);
        emailLogRepository.deleteAll(oldEmails);
    }

    private boolean shouldRetry(EmailLog emailLog) {
        if (emailLog.getLastRetryAt() == null) {
            return true;
        }
        return emailLog.getLastRetryAt().plusMinutes(RETRY_DELAY_MINUTES).isBefore(LocalDateTime.now());
    }

    private void retryEmail(EmailLog emailLog) {
        emailLog.setStatus(EmailStatus.RETRYING);
        emailLog.setRetryCount(emailLog.getRetryCount() + 1);
        emailLog.setLastRetryAt(LocalDateTime.now());
        emailLogRepository.save(emailLog);

        rabbitTemplate.convertAndSend(EXCHANGE_EMAIL, ROUTING_KEY_EMAIL, emailLog);
    }

    private void sendEmailDirectly(EmailLog emailLog) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(emailLog.getRecipient());
        helper.setSubject(emailLog.getSubject());
        helper.setText(emailLog.getContent(), true);

        mailSender.send(message);
    }

    private EmailResponse createEmailResponse(EmailLog emailLog) {
        EmailResponse response = new EmailResponse();
        response.setId(emailLog.getId());
        response.setRecipient(emailLog.getRecipient());
        response.setSubject(emailLog.getSubject());
        response.setTemplateName(emailLog.getTemplateName());
        response.setStatus(emailLog.getStatus());
        response.setErrorMessage(emailLog.getErrorMessage());
        response.setRetryCount(emailLog.getRetryCount());
        response.setCreatedAt(emailLog.getCreatedAt());
        response.setSentAt(emailLog.getSentAt());
        response.setMessageId(emailLog.getMessageId());
        return response;
    }
} 