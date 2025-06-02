package org.ecommerce.paymentservice.dtos;

import lombok.Data;
import org.ecommerce.paymentservice.models.PaymentMethod;
import org.ecommerce.paymentservice.models.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionId;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
} 