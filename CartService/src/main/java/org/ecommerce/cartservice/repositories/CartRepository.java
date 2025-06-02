package org.ecommerce.cartservice.repositories;

import org.ecommerce.cartservice.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    Optional<Cart> findByUserIdAndActive(Long userId, Boolean active);
    
    @Query("SELECT c FROM Cart c WHERE c.active = true AND c.expiresAt < :expiryTime")
    List<Cart> findExpiredCarts(@Param("expiryTime") LocalDateTime expiryTime);
    
    List<Cart> findByUserId(Long userId);
    
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.userId = :userId AND c.active = true")
    long countActiveCartsByUser(@Param("userId") Long userId);
} 