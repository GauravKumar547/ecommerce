package com.ecommerce.productcatalogservice.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "inventory")
public class Inventory extends BaseModel {

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer reservedQuantity = 0;

    @Column(nullable = false)
    private Integer minStockLevel;

    @Column(nullable = false)
    private Integer maxStockLevel;

    @Column(nullable = false)
    private Boolean lowStockAlert = false;

    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public void updateLowStockAlert() {
        this.lowStockAlert = this.quantity <= this.minStockLevel;
    }
} 