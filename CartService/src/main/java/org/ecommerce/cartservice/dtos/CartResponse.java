package org.ecommerce.cartservice.dtos;

import lombok.Data;
import org.ecommerce.cartservice.models.Cart;
import org.ecommerce.cartservice.models.CartItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CartResponse {
    private Long id;
    private Long userId;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private Integer totalItems;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;

    public static CartResponse fromEntity(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUserId());
        response.setItems(cart.getItems().stream()
                .map(CartItemResponse::fromEntity)
                .collect(Collectors.toList()));
        response.setTotalAmount(cart.getTotalAmount());
        response.setTotalItems(cart.getTotalItems());
        response.setActive(cart.getActive());
        response.setCreatedAt(cart.getCreatedAt());
        response.setUpdatedAt(cart.getUpdatedAt());
        response.setExpiresAt(cart.getExpiresAt());
        return response;
    }
}

@Data
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private String productImage;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal finalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CartItemResponse fromEntity(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setProductName(item.getProductName());
        response.setProductSku(item.getProductSku());
        response.setProductImage(item.getProductImage());
        response.setUnitPrice(item.getUnitPrice());
        response.setQuantity(item.getQuantity());
        response.setSubtotal(item.getSubtotal());
        response.setDiscount(item.getDiscount());
        response.setFinalPrice(item.getFinalPrice());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        return response;
    }
} 