package org.ecommerce.orderservice.services;

import lombok.extern.slf4j.Slf4j;
import org.ecommerce.orderservice.config.RabbitMQConfig;
import org.ecommerce.orderservice.events.OrderEvent;
import org.ecommerce.orderservice.models.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderCreated(Order order) {
        OrderEvent event = createOrderEvent(order, "ORDER_CREATED");
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ORDERS, 
                RabbitMQConfig.ROUTING_KEY_ORDER_CREATED, event);
        log.info("Published order created event for order: {}", order.getOrderNumber());
    }

    public void publishOrderStatusChanged(Order order) {
        OrderEvent event = createOrderEvent(order, "ORDER_UPDATED");
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ORDERS, 
                RabbitMQConfig.ROUTING_KEY_ORDER_UPDATED, event);
        log.info("Published order status changed event for order: {}", order.getOrderNumber());
    }

    public void publishOrderShipped(Order order) {
        OrderEvent event = createOrderEvent(order, "ORDER_SHIPPED");
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ORDERS, 
                RabbitMQConfig.ROUTING_KEY_ORDER_SHIPPED, event);
        log.info("Published order shipped event for order: {}", order.getOrderNumber());
    }

    public void publishOrderCancelled(Order order) {
        OrderEvent event = createOrderEvent(order, "ORDER_CANCELLED");
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ORDERS, 
                RabbitMQConfig.ROUTING_KEY_ORDER_CANCELLED, event);
        log.info("Published order cancelled event for order: {}", order.getOrderNumber());
    }

    private OrderEvent createOrderEvent(Order order, String eventType) {
        OrderEvent event = new OrderEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(eventType);
        event.setEventTime(LocalDateTime.now());
        event.setOrderId(order.getId());
        event.setOrderNumber(order.getOrderNumber());
        event.setUserId(order.getUserId());
        event.setOrderStatus(order.getStatus());
        event.setPaymentStatus(order.getPaymentStatus());
        event.setTotalAmount(order.getFinalAmount());
        event.setTrackingNumber(order.getTrackingNumber());
        return event;
    }
} 