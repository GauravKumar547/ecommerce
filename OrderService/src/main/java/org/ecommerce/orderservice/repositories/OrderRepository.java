package org.ecommerce.orderservice.repositories;

import org.ecommerce.orderservice.models.Order;
import org.ecommerce.orderservice.models.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime dateTime);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt < :dateTime")
    List<Order> findStaleOrders(OrderStatus status, LocalDateTime dateTime);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId AND o.status = :status")
    long countByUserIdAndStatus(Long userId, OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.updatedAt < :lastUpdated")
    List<Order> findOrdersForStatusUpdate(OrderStatus status, LocalDateTime lastUpdated);
} 