package org.ecommerce.cartservice.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddItemRequest {
    
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Product name is required")
    private String productName;

    @NotNull(message = "Product SKU is required")
    private String productSku;

    @NotNull(message = "Product image URL is required")
    private String productImage;

    @NotNull(message = "Unit price is required")
    @Min(value = 0, message = "Unit price must be greater than or equal to 0")
    private BigDecimal unitPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private BigDecimal discount = BigDecimal.ZERO;
} 