package org.ecommerce.cartservice.events;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartEvent {
    private String eventId;
    private String eventType;
    private LocalDateTime eventTime;
    private Long cartId;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private Integer totalItems;
} 