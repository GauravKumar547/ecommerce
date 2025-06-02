package org.ecommerce.emailservice.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "email_logs")
public class EmailLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String templateName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailStatus status;

    private String errorMessage;

    private Integer retryCount = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime sentAt;

    private LocalDateTime lastRetryAt;

    @Column(nullable = false)
    private String messageId;
} 