package org.ecommerce.emailservice.models;

public enum EmailStatus {
    PENDING,
    QUEUED,
    SENT,
    FAILED,
    RETRYING,
    DEAD_LETTER
} 