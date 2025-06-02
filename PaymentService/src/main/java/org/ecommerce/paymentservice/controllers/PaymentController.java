package org.ecommerce.paymentservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ecommerce.paymentservice.dtos.PaymentRequest;
import org.ecommerce.paymentservice.dtos.PaymentResponse;
import org.ecommerce.paymentservice.dtos.RefundRequest;
import org.ecommerce.paymentservice.services.PaymentService;
import org.ecommerce.paymentservice.utils.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment Management", description = "APIs for managing payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @Operation(summary = "Process payment", description = "Process a new payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(@RequestBody PaymentRequest request) {
        ApiResponse<PaymentResponse> apiResponse = new ApiResponse<>();
        PaymentResponse response = paymentService.processPayment(request);
        apiResponse.setData(response).setStatus(HttpStatus.CREATED);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment", description = "Get payment details by ID")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable Long id) {
        ApiResponse<PaymentResponse> apiResponse = new ApiResponse<>();
        PaymentResponse response = paymentService.getPaymentById(id);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order", description = "Get payment details by order ID")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrder(@PathVariable Long orderId) {
        ApiResponse<PaymentResponse> apiResponse = new ApiResponse<>();
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user payments", description = "Get paginated list of payments for a user")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getUserPayments(
            @PathVariable Long userId,
            Pageable pageable) {
        ApiResponse<Page<PaymentResponse>> apiResponse = new ApiResponse<>();
        Page<PaymentResponse> response = paymentService.getPaymentsByUserId(userId, pageable);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/refund")
    @Operation(summary = "Refund payment", description = "Process a refund for a payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(@RequestBody RefundRequest request) {
        ApiResponse<PaymentResponse> apiResponse = new ApiResponse<>();
        PaymentResponse response = paymentService.refundPayment(request);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel payment", description = "Cancel a pending payment")
    public ResponseEntity<ApiResponse<Void>> cancelPayment(@PathVariable Long id) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        paymentService.cancelPayment(id);
        apiResponse.setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @GetMapping("/history/{userId}")
    @Operation(summary = "Get payment history", description = "Get complete payment history for a user")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentHistory(@PathVariable Long userId) {
        ApiResponse<List<PaymentResponse>> apiResponse = new ApiResponse<>();
        List<PaymentResponse> response = paymentService.getPaymentHistory(userId);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/{id}/retry")
    @Operation(summary = "Retry payment", description = "Retry a failed payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> retryPayment(@PathVariable Long id) {
        ApiResponse<PaymentResponse> apiResponse = new ApiResponse<>();
        PaymentResponse response = paymentService.retryPayment(id);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @GetMapping("/{id}/validate")
    @Operation(summary = "Validate payment", description = "Validate payment status with payment provider")
    public ResponseEntity<ApiResponse<Boolean>> validatePayment(@PathVariable Long id) {
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();
        Boolean response = paymentService.validatePayment(id);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }
}
