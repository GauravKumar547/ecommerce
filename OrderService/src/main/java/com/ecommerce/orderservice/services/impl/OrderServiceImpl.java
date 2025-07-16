package com.ecommerce.orderservice.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import com.ecommerce.orderservice.dtos.*;
import com.ecommerce.orderservice.models.*;
import com.ecommerce.orderservice.repositories.OrderRepository;
import com.ecommerce.orderservice.services.OrderEventPublisher;
import com.ecommerce.orderservice.services.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    @Value("${app.order.stale-timeout-hours}")
    private int staleOrderTimeoutHours;

    @Value("${app.tax.rate}")
    private BigDecimal taxRate;

    @Value("${app.shipping.base-cost}")
    private BigDecimal baseShippingCost;

    public OrderServiceImpl(OrderRepository orderRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        // Set shipping address
        ShippingAddress shippingAddress = mapShippingAddressFromRequest(request.getShippingAddress());
        order.setShippingAddress(shippingAddress);

        // Process order items
        List<OrderItem> orderItems = new ArrayList<>();
        for( OrderItemRequest item : request.getItems()) {
         orderItems.add(createOrderItem(item, order));
        }
        orderItems.forEach(order::addOrderItem);

        // Calculate order totals
        calculateOrderTotals(order);

        // Save the order
        order = orderRepository.save(order);

        // Publish order created event
        eventPublisher.publishOrderCreated(order);

        return mapOrderToResponse(order);
    }

    @Override
    @Cacheable(value = "orders", key = "#id")
    public OrderResponse getOrderById(Long id) {
        Order order = findOrderById(id);
        return mapOrderToResponse(order);
    }

    @Override
    @Cacheable(value = "orders", key = "#orderNumber")
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with number: " + orderNumber));
        return mapOrderToResponse(order);
    }

    @Override
    public Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(this::mapOrderToResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "orders", key = "#id")
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = findOrderById(id);
        order.setStatus(status);
        
        if (status == OrderStatus.SHIPPED) {
            order.setShippedAt(LocalDateTime.now());
        } else if (status == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        order = orderRepository.save(order);
        eventPublisher.publishOrderStatusChanged(order);
        
        return mapOrderToResponse(order);
    }

    @Override
    @Transactional
    @CacheEvict(value = "orders", key = "#id")
    public OrderResponse updateShippingInfo(Long id, String trackingNumber) {
        Order order = findOrderById(id);
        order.setTrackingNumber(trackingNumber);
        order.setStatus(OrderStatus.SHIPPED);
        order.setShippedAt(LocalDateTime.now());
        
        order = orderRepository.save(order);
        eventPublisher.publishOrderShipped(order);
        
        return mapOrderToResponse(order);
    }

    @Override
    @Transactional
    @CacheEvict(value = "orders", key = "#id")
    public void cancelOrder(Long id) {
        Order order = findOrderById(id);
        
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel order that has been shipped or delivered");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        eventPublisher.publishOrderCancelled(order);
    }

    @Override
    @Scheduled(fixedDelayString = "${app.order.stale-check-interval}")
    @Transactional
    public void processStaleOrders() {
        LocalDateTime staleThreshold = LocalDateTime.now().minusHours(staleOrderTimeoutHours);
        List<Order> staleOrders = orderRepository.findStaleOrders(OrderStatus.PENDING, staleThreshold);
        
        staleOrders.forEach(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            eventPublisher.publishOrderCancelled(order);
        });
    }

    @Override
    @Transactional
    public void updateOrderStatusFromPayment(String paymentId, String status) {
        Order order = orderRepository.findAll().stream()
                .filter(o -> paymentId.equals(o.getPaymentId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Order not found for payment: " + paymentId));

        PaymentStatus paymentStatus = PaymentStatus.valueOf(status);
        order.setPaymentStatus(paymentStatus);

        switch (paymentStatus) {
            case PAID -> order.setStatus(OrderStatus.CONFIRMED);
            case FAILED -> order.setStatus(OrderStatus.CANCELLED);
            case REFUNDED -> order.setStatus(OrderStatus.REFUNDED);
        }

        orderRepository.save(order);
        eventPublisher.publishOrderStatusChanged(order);
    }

    @Override
    @Transactional
    public void updateOrderStatusFromShipping(String trackingNumber, String status) {
        Order order = orderRepository.findAll().stream()
                .filter(o -> trackingNumber.equals(o.getTrackingNumber()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Order not found for tracking: " + trackingNumber));

        switch (status.toUpperCase()) {
            case "SHIPPED" -> {
                order.setStatus(OrderStatus.SHIPPED);
                order.setShippedAt(LocalDateTime.now());
            }
            case "DELIVERED" -> {
                order.setStatus(OrderStatus.DELIVERED);
                order.setDeliveredAt(LocalDateTime.now());
            }
        }

        orderRepository.save(order);
        eventPublisher.publishOrderStatusChanged(order);
    }

    @Override
    public long getOrderCountByUserAndStatus(Long userId, OrderStatus status) {
        return orderRepository.countByUserIdAndStatus(userId, status);
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ShippingAddress mapShippingAddressFromRequest(ShippingAddressRequest request) {
        ShippingAddress address = new ShippingAddress();
        address.setFullName(request.getFullName());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        address.setPhone(request.getPhone());
        address.setEmail(request.getEmail());
        address.setSpecialInstructions(request.getSpecialInstructions());
        return address;
    }

    private OrderItem createOrderItem(OrderItemRequest request, Order order) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(request.getProductId());
        item.setQuantity(request.getQuantity());
        return item;
    }

    private void calculateOrderTotals(Order order) {
        BigDecimal subtotal = order.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(subtotal);
        order.setShippingCost(calculateShippingCost(order));
        order.setTaxAmount(subtotal.multiply(taxRate));
        order.setFinalAmount(subtotal.add(order.getShippingCost()).add(order.getTaxAmount()));
    }

    private BigDecimal calculateShippingCost(Order order) {
        // TODO: Implement more sophisticated shipping cost calculation
        return baseShippingCost;
    }

    private OrderResponse mapOrderToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserId(order.getUserId());
        response.setItems(mapOrderItems(order.getItems()));
        response.setTotalAmount(order.getTotalAmount());
        response.setShippingCost(order.getShippingCost());
        response.setTaxAmount(order.getTaxAmount());
        response.setFinalAmount(order.getFinalAmount());
        response.setShippingAddress(mapShippingAddressToResponse(order.getShippingAddress()));
        response.setStatus(order.getStatus());
        response.setPaymentId(order.getPaymentId());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setTrackingNumber(order.getTrackingNumber());
        response.setShippedAt(order.getShippedAt());
        response.setDeliveredAt(order.getDeliveredAt());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }

    private List<OrderItemResponse> mapOrderItems(List<OrderItem> items) {
        return items.stream()
                .map(this::mapOrderItemToResponse)
                .collect(Collectors.toList());
    }

    private OrderItemResponse mapOrderItemToResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setProductName(item.getProductName());
        response.setProductSku(item.getProductSku());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setSubtotal(item.getSubtotal());
        response.setDiscount(item.getDiscount());
        response.setFinalPrice(item.getFinalPrice());
        return response;
    }

    private ShippingAddressResponse mapShippingAddressToResponse(ShippingAddress address) {
        ShippingAddressResponse response = new ShippingAddressResponse();
        response.setFullName(address.getFullName());
        response.setAddressLine1(address.getAddressLine1());
        response.setAddressLine2(address.getAddressLine2());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setCountry(address.getCountry());
        response.setPostalCode(address.getPostalCode());
        response.setPhone(address.getPhone());
        response.setEmail(address.getEmail());
        response.setSpecialInstructions(address.getSpecialInstructions());
        return response;
    }
} 