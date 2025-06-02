package org.ecommerce.paymentservice.repositories;

import org.ecommerce.paymentservice.models.Payment;
import org.ecommerce.paymentservice.models.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
    Optional<Payment> findByOrderId(Long orderId);
    List<Payment> findByUserId(Long userId);
    Page<Payment> findByUserId(Long userId, Pageable pageable);
    List<Payment> findByStatus(PaymentStatus status);
    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByPaymentIntentId(String paymentIntentId);
} 