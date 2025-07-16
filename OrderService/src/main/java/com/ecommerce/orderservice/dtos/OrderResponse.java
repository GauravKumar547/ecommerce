package com.ecommerce.orderservice.dtos;

import lombok.Data;
import com.ecommerce.orderservice.models.OrderStatus;
import com.ecommerce.orderservice.models.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long userId;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private BigDecimal shippingCost;
    private BigDecimal taxAmount;
    private BigDecimal finalAmount;
    private ShippingAddressResponse shippingAddress;
    private OrderStatus status;
    private String paymentId;
    private PaymentStatus paymentStatus;
    private String trackingNumber;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 