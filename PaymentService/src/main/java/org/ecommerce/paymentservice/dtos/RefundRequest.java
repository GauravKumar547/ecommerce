package org.ecommerce.paymentservice.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundRequest {
    private Long paymentId;
    private BigDecimal amount;
    private String reason;
    private String idempotencyKey;
} 