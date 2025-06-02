package org.ecommerce.paymentservice.services;

import org.ecommerce.paymentservice.dtos.PaymentRequest;
import org.ecommerce.paymentservice.dtos.PaymentResponse;
import org.ecommerce.paymentservice.dtos.RefundRequest;
import org.ecommerce.paymentservice.models.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    PaymentResponse getPaymentById(Long id);
    PaymentResponse getPaymentByOrderId(Long orderId);
    Page<PaymentResponse> getPaymentsByUserId(Long userId, Pageable pageable);
    PaymentResponse refundPayment(RefundRequest request);
    void cancelPayment(Long paymentId);
    List<PaymentResponse> getPaymentHistory(Long userId);
    PaymentResponse retryPayment(Long paymentId);
    boolean validatePayment(Long paymentId);
} 