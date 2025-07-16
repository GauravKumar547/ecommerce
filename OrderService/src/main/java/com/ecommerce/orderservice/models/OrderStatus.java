package com.ecommerce.orderservice.models;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED,
    ON_HOLD,
    RETURNED
} 