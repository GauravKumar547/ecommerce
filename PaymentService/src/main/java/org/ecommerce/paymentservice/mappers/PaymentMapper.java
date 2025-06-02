package org.ecommerce.paymentservice.mappers;

import org.ecommerce.paymentservice.dtos.PaymentRequest;
import org.ecommerce.paymentservice.dtos.PaymentResponse;
import org.ecommerce.paymentservice.models.Payment;
import org.ecommerce.paymentservice.models.PaymentStatus;

public class PaymentMapper {

    public static Payment toEntity(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setIdempotencyKey(request.getIdempotencyKey());
        return payment;
    }

    public static PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setOrderId(payment.getOrderId());
        response.setUserId(payment.getUserId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setStatus(payment.getStatus());
        response.setTransactionId(payment.getTransactionId());
        response.setErrorMessage(payment.getErrorMessage());
        response.setCreatedAt(payment.getCreatedAt());
        response.setCompletedAt(payment.getCompletedAt());
        return response;
    }
} 