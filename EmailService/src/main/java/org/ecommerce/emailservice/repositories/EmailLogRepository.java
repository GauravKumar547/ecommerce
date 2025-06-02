package org.ecommerce.emailservice.repositories;

import org.ecommerce.emailservice.models.EmailLog;
import org.ecommerce.emailservice.models.EmailStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    Page<EmailLog> findByRecipient(String recipient, Pageable pageable);
    List<EmailLog> findByStatus(EmailStatus status);
    List<EmailLog> findByStatusAndRetryCountLessThan(EmailStatus status, Integer maxRetries);
    List<EmailLog> findByStatusAndLastRetryAtBefore(EmailStatus status, LocalDateTime time);
    Page<EmailLog> findByTemplateName(String templateName, Pageable pageable);
} 