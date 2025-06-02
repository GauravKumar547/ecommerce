package org.ecommerce.cartservice.services;

import lombok.extern.slf4j.Slf4j;
import org.ecommerce.cartservice.config.RabbitMQConfig;
import org.ecommerce.cartservice.events.CartEvent;
import org.ecommerce.cartservice.models.Cart;
import org.ecommerce.cartservice.models.CartItem;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class CartEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public CartEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCartUpdated(Cart cart) {
        CartEvent event = createCartEvent(cart, "CART_UPDATED", null);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_CARTS,
                RabbitMQConfig.ROUTING_KEY_CART_UPDATED, event);
        log.info("Published cart updated event for cart: {}", cart.getId());
    }

    public void publishCartCleared(Cart cart) {
        CartEvent event = createCartEvent(cart, "CART_CLEARED", null);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_CARTS,
                RabbitMQConfig.ROUTING_KEY_CART_CLEARED, event);
        log.info("Published cart cleared event for cart: {}", cart.getId());
    }

    public void publishItemAdded(Cart cart, CartItem item) {
        CartEvent event = createCartEvent(cart, "CART_ITEM_ADDED", item);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_CARTS,
                RabbitMQConfig.ROUTING_KEY_CART_ITEM_ADDED, event);
        log.info("Published item added event for cart: {}, item: {}", cart.getId(), item.getId());
    }

    public void publishItemRemoved(Cart cart, CartItem item) {
        CartEvent event = createCartEvent(cart, "CART_ITEM_REMOVED", item);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_CARTS,
                RabbitMQConfig.ROUTING_KEY_CART_ITEM_REMOVED, event);
        log.info("Published item removed event for cart: {}, item: {}", cart.getId(), item.getId());
    }

    private CartEvent createCartEvent(Cart cart, String eventType, CartItem item) {
        CartEvent event = new CartEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(eventType);
        event.setEventTime(LocalDateTime.now());
        event.setCartId(cart.getId());
        event.setUserId(cart.getUserId());
        event.setTotalAmount(cart.getTotalAmount());
        event.setTotalItems(cart.getTotalItems());
        
        if (item != null) {
            event.setProductId(item.getProductId());
            event.setQuantity(item.getQuantity());
        }
        
        return event;
    }
} 