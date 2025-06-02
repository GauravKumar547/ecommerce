package org.ecommerce.orderservice.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "Order must contain at least one item")
    @Size(min = 1, message = "Order must contain at least one item")
    private List<@Valid OrderItemRequest> items;

    @NotNull(message = "Shipping address is required")
    @Valid
    private ShippingAddressRequest shippingAddress;

    private String specialInstructions;
} 