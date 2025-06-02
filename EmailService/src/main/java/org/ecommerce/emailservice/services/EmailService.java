package org.ecommerce.emailservice.services;

import org.ecommerce.emailservice.dtos.EmailRequest;
import org.ecommerce.emailservice.dtos.EmailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmailService {
    EmailResponse sendEmail(EmailRequest request);
    EmailResponse getEmailStatus(Long id);
    Page<EmailResponse> getEmailsByRecipient(String recipient, Pageable pageable);
    void retryFailedEmails();
    void processDeadLetterQueue();
    void cleanupOldEmails(int daysToKeep);
} 