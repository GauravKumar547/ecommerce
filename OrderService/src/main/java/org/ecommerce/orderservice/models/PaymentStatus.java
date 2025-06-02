package org.ecommerce.orderservice.models;

public enum PaymentStatus {
    PENDING,
    AUTHORIZED,
    PAID,
    FAILED,
    REFUNDED,
    PARTIALLY_REFUNDED,
    CANCELLED
} 