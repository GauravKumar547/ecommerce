package org.ecommerce.emailservice.dtos;

import lombok.Data;
import org.ecommerce.emailservice.models.EmailStatus;

import java.time.LocalDateTime;

@Data
public class EmailResponse {
    private Long id;
    private String recipient;
    private String subject;
    private String templateName;
    private EmailStatus status;
    private String errorMessage;
    private Integer retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private String messageId;
} 