package org.ecommerce.cartservice.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.ecommerce.cartservice.dtos.AddItemRequest;
import org.ecommerce.cartservice.dtos.CartResponse;
import org.ecommerce.cartservice.dtos.UpdateItemRequest;
import org.ecommerce.cartservice.models.Cart;
import org.ecommerce.cartservice.models.CartItem;
import org.ecommerce.cartservice.repositories.CartRepository;
import org.ecommerce.cartservice.services.CartEventPublisher;
import org.ecommerce.cartservice.services.CartService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartEventPublisher eventPublisher;

    @Value("${app.cart.expiry-hours}")
    private int cartExpiryHours;

    public CartServiceImpl(CartRepository cartRepository, CartEventPublisher eventPublisher) {
        this.cartRepository = cartRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "carts", key = "#userId + '_active'")
    public CartResponse getOrCreateCart(Long userId) {
        Cart cart = cartRepository.findByUserIdAndActive(userId, true)
                .orElseGet(() -> createNewCart(userId));
        return CartResponse.fromEntity(cart);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "carts", key = "#cartId")
    public CartResponse getCartById(Long cartId) {
        Cart cart = findCartById(cartId);
        return CartResponse.fromEntity(cart);
    }

    @Override
    @Transactional
    @CacheEvict(value = "carts", key = "#userId + '_active'")
    public CartResponse addItem(Long userId, AddItemRequest request) {
        Cart cart = getOrCreateActiveCart(userId);
        
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.setUnitPrice(request.getUnitPrice());
            item.setDiscount(request.getDiscount());
            eventPublisher.publishCartUpdated(cart);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductId(request.getProductId());
            newItem.setProductName(request.getProductName());
            newItem.setProductSku(request.getProductSku());
            newItem.setProductImage(request.getProductImage());
            newItem.setUnitPrice(request.getUnitPrice());
            newItem.setQuantity(request.getQuantity());
            newItem.setDiscount(request.getDiscount());
            cart.addItem(newItem);
            eventPublisher.publishItemAdded(cart, newItem);
        }

        cart = cartRepository.save(cart);
        return CartResponse.fromEntity(cart);
    }

    @Override
    @Transactional
    @CacheEvict(value = "carts", key = "#userId + '_active'")
    public CartResponse updateItem(Long userId, Long itemId, UpdateItemRequest request) {
        Cart cart = getOrCreateActiveCart(userId);
        
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found with id: " + itemId));

        item.setQuantity(request.getQuantity());
        if (request.getDiscount() != null) {
            item.setDiscount(request.getDiscount());
        }

        cart = cartRepository.save(cart);
        eventPublisher.publishCartUpdated(cart);
        
        return CartResponse.fromEntity(cart);
    }

    @Override
    @Transactional
    @CacheEvict(value = "carts", key = "#userId + '_active'")
    public CartResponse removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateActiveCart(userId);
        
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found with id: " + itemId));

        cart.removeItem(item);
        cart = cartRepository.save(cart);
        eventPublisher.publishItemRemoved(cart, item);
        
        return CartResponse.fromEntity(cart);
    }

    @Override
    @Transactional
    @CacheEvict(value = "carts", key = "#userId + '_active'")
    public void clearCart(Long userId) {
        Cart cart = getOrCreateActiveCart(userId);
        cart.clear();
        cartRepository.save(cart);
        eventPublisher.publishCartCleared(cart);
    }

    @Override
    @Transactional
    @CacheEvict(value = "carts", allEntries = true)
    public void deactivateCart(Long cartId) {
        Cart cart = findCartById(cartId);
        cart.setActive(false);
        cartRepository.save(cart);
        eventPublisher.publishCartUpdated(cart);
    }

    @Override
    @Scheduled(fixedDelayString = "${app.cart.cleanup-interval}")
    @Transactional
    @CacheEvict(value = "carts", allEntries = true)
    public void processExpiredCarts() {
        LocalDateTime expiryTime = LocalDateTime.now().minusHours(cartExpiryHours);
        List<Cart> expiredCarts = cartRepository.findExpiredCarts(expiryTime);
        
        expiredCarts.forEach(cart -> {
            cart.setActive(false);
            cartRepository.save(cart);
            eventPublisher.publishCartUpdated(cart);
            log.info("Deactivated expired cart: {}", cart.getId());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveCartCount(Long userId) {
        return cartRepository.countActiveCartsByUser(userId);
    }

    private Cart getOrCreateActiveCart(Long userId) {
        return cartRepository.findByUserIdAndActive(userId, true)
                .orElseGet(() -> createNewCart(userId));
    }

    private Cart createNewCart(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setExpiresAt(LocalDateTime.now().plusHours(cartExpiryHours));
        return cartRepository.save(cart);
    }

    private Cart findCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + cartId));
    }
} 