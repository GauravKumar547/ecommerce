package org.ecommerce.paymentservice.dtos;

import lombok.Data;
import org.ecommerce.paymentservice.models.PaymentMethod;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private String paymentToken;
    private String idempotencyKey;
} 