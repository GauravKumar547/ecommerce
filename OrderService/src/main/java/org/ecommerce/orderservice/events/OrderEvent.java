package org.ecommerce.orderservice.events;

import lombok.Data;
import org.ecommerce.orderservice.models.OrderStatus;
import org.ecommerce.orderservice.models.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderEvent {
    private String eventId;
    private String eventType;
    private LocalDateTime eventTime;
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private BigDecimal totalAmount;
    private String trackingNumber;
} 