package org.ecommerce.cartservice.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Data
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer totalItems = 0;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime expiresAt;

    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
        recalculateTotals();
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
        recalculateTotals();
    }

    public void clear() {
        items.clear();
        recalculateTotals();
    }

    private void recalculateTotals() {
        this.totalAmount = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.totalItems = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
} 