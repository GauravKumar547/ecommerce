package com.ecommerce.orderservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.ecommerce.orderservice.dtos.CreateOrderRequest;
import com.ecommerce.orderservice.dtos.OrderResponse;
import com.ecommerce.orderservice.models.OrderStatus;
import com.ecommerce.orderservice.services.OrderService;
import com.ecommerce.orderservice.utils.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>();
        OrderResponse response = orderService.createOrder(request);
        apiResponse.setData(response).setStatus(HttpStatus.CREATED);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Get order details by order ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>();
        OrderResponse response = orderService.getOrderById(id);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by number", description = "Get order details by order number")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(
            @Parameter(description = "Order number") @PathVariable String orderNumber) {
        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>();
        OrderResponse response = orderService.getOrderByOrderNumber(orderNumber);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user orders", description = "Get all orders for a specific user")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrdersByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId,
            Pageable pageable) {
        ApiResponse<Page<OrderResponse>> apiResponse = new ApiResponse<>();
        Page<OrderResponse> response = orderService.getOrdersByUserId(userId, pageable);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Update the status of an order")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable Long id,
            @Parameter(description = "New order status") @RequestParam OrderStatus status) {
        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>();
        OrderResponse response = orderService.updateOrderStatus(id, status);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PutMapping("/{id}/shipping")
    @Operation(summary = "Update shipping info", description = "Update order shipping information")
    public ResponseEntity<ApiResponse<OrderResponse>> updateShippingInfo(
            @Parameter(description = "Order ID") @PathVariable Long id,
            @Parameter(description = "Tracking number") @RequestParam String trackingNumber) {
        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>();
        OrderResponse response = orderService.updateShippingInfo(id, trackingNumber);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an existing order")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        orderService.cancelOrder(id);
        apiResponse.setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/process-stale")
    @Operation(summary = "Process stale orders", description = "Process and cancel stale orders")
    public ResponseEntity<ApiResponse<Void>> processStaleOrders() {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        orderService.processStaleOrders();
        apiResponse.setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PutMapping("/payment/{paymentId}/status")
    @Operation(summary = "Update payment status", description = "Update order status based on payment status")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatusFromPayment(
            @Parameter(description = "Payment ID") @PathVariable String paymentId,
            @Parameter(description = "Payment status") @RequestParam String status) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        orderService.updateOrderStatusFromPayment(paymentId, status);
        apiResponse.setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PutMapping("/shipping/{trackingNumber}/status")
    @Operation(summary = "Update shipping status", description = "Update order status based on shipping status")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatusFromShipping(
            @Parameter(description = "Tracking number") @PathVariable String trackingNumber,
            @Parameter(description = "Shipping status") @RequestParam String status) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        orderService.updateOrderStatusFromShipping(trackingNumber, status);
        apiResponse.setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Get order count", description = "Get count of orders by user and status")
    public ResponseEntity<ApiResponse<Long>> getOrderCount(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Order status") @RequestParam OrderStatus status) {
        ApiResponse<Long> apiResponse = new ApiResponse<>();
        Long count = orderService.getOrderCountByUserAndStatus(userId, status);
        apiResponse.setData(count).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }
} 