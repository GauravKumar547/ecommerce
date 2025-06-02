package org.ecommerce.paymentservice.services.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.persistence.EntityNotFoundException;
import org.ecommerce.paymentservice.dtos.PaymentRequest;
import org.ecommerce.paymentservice.dtos.PaymentResponse;
import org.ecommerce.paymentservice.dtos.RefundRequest;
import org.ecommerce.paymentservice.mappers.PaymentMapper;
import org.ecommerce.paymentservice.models.Payment;
import org.ecommerce.paymentservice.models.PaymentStatus;
import org.ecommerce.paymentservice.repositories.PaymentRepository;
import org.ecommerce.paymentservice.services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        // Check for idempotency
        if (paymentRepository.findByIdempotencyKey(request.getIdempotencyKey()).isPresent()) {
            throw new IllegalStateException("Payment already processed with this idempotency key");
        }

        Payment payment = PaymentMapper.toEntity(request);

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(request.getAmount().multiply(java.math.BigDecimal.valueOf(100)).longValue())
                    .setCurrency(request.getCurrency())
                    .setPaymentMethod(request.getPaymentToken())
                    .setConfirm(true)
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            payment.setPaymentIntentId(paymentIntent.getId());
            payment.setTransactionId(paymentIntent.getLatestCharge());

            if ("succeeded".equals(paymentIntent.getStatus())) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setCompletedAt(java.time.LocalDateTime.now());
            } else if ("requires_payment_method".equals(paymentIntent.getStatus())) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setErrorMessage("Payment method failed");
            }

        } catch (StripeException e) {
            logger.error("Error processing payment", e);
            payment.setStatus(PaymentStatus.FAILED);
            payment.setErrorMessage(e.getMessage());
        }

        payment = paymentRepository.save(payment);
        return PaymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
        return PaymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for order: " + orderId));
        return PaymentMapper.toResponse(payment);
    }

    @Override
    public Page<PaymentResponse> getPaymentsByUserId(Long userId, Pageable pageable) {
        return paymentRepository.findByUserId(userId, pageable)
                .map(PaymentMapper::toResponse);
    }

    @Override
    @Transactional
    public PaymentResponse refundPayment(RefundRequest request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + request.getPaymentId()));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Payment cannot be refunded. Current status: " + payment.getStatus());
        }

        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(payment.getPaymentIntentId())
                    .setAmount(request.getAmount().multiply(java.math.BigDecimal.valueOf(100)).longValue())
                    .build();

            Refund refund = Refund.create(params);

            if ("succeeded".equals(refund.getStatus())) {
                payment.setStatus(PaymentStatus.REFUNDED);
                payment.setRefundAmount(request.getAmount());
                payment.setRefundReason(request.getReason());
                payment.setRefundedAt(java.time.LocalDateTime.now());
                payment = paymentRepository.save(payment);
            }
        } catch (StripeException e) {
            logger.error("Error processing refund", e);
            throw new RuntimeException("Failed to process refund: " + e.getMessage());
        }

        return PaymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public void cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment cannot be cancelled. Current status: " + payment.getStatus());
        }

        try {
            PaymentIntent.retrieve(payment.getPaymentIntentId()).cancel();
            payment.setStatus(PaymentStatus.CANCELLED);
            paymentRepository.save(payment);
        } catch (StripeException e) {
            logger.error("Error cancelling payment", e);
            throw new RuntimeException("Failed to cancel payment: " + e.getMessage());
        }
    }

    @Override
    public List<PaymentResponse> getPaymentHistory(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(PaymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentResponse retryPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.FAILED) {
            throw new IllegalStateException("Payment cannot be retried. Current status: " + payment.getStatus());
        }

        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(payment.getPaymentIntentId());
            paymentIntent.confirm();

            if ("succeeded".equals(paymentIntent.getStatus())) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setCompletedAt(java.time.LocalDateTime.now());
                payment = paymentRepository.save(payment);
            }
        } catch (StripeException e) {
            logger.error("Error retrying payment", e);
            throw new RuntimeException("Failed to retry payment: " + e.getMessage());
        }

        return PaymentMapper.toResponse(payment);
    }

    @Override
    public boolean validatePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + paymentId));

        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(payment.getPaymentIntentId());
            return "succeeded".equals(paymentIntent.getStatus());
        } catch (StripeException e) {
            logger.error("Error validating payment", e);
            return false;
        }
    }
}
