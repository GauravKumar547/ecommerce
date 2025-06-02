package org.ecommerce.cartservice.services;

import org.ecommerce.cartservice.dtos.AddItemRequest;
import org.ecommerce.cartservice.dtos.CartResponse;
import org.ecommerce.cartservice.dtos.UpdateItemRequest;

public interface CartService {
    
    CartResponse getOrCreateCart(Long userId);
    
    CartResponse getCartById(Long cartId);
    
    CartResponse addItem(Long userId, AddItemRequest request);
    
    CartResponse updateItem(Long userId, Long itemId, UpdateItemRequest request);
    
    CartResponse removeItem(Long userId, Long itemId);
    
    void clearCart(Long userId);
    
    void deactivateCart(Long cartId);
    
    void processExpiredCarts();
    
    long getActiveCartCount(Long userId);
} 