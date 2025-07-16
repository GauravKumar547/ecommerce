package com.ecommerce.orderservice.services;

import com.ecommerce.orderservice.dtos.CreateOrderRequest;
import com.ecommerce.orderservice.dtos.OrderResponse;
import com.ecommerce.orderservice.models.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    
    OrderResponse getOrderById(Long id);
    
    OrderResponse getOrderByOrderNumber(String orderNumber);
    
    Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable);
    
    OrderResponse updateOrderStatus(Long id, OrderStatus status);
    
    OrderResponse updateShippingInfo(Long id, String trackingNumber);
    
    void cancelOrder(Long id);
    
    void processStaleOrders();
    
    void updateOrderStatusFromPayment(String paymentId, String status);
    
    void updateOrderStatusFromShipping(String trackingNumber, String status);
    
    long getOrderCountByUserAndStatus(Long userId, OrderStatus status);
} 