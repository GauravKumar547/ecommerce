package com.ecommerce.productcatalogservice.repos;

import com.ecommerce.productcatalogservice.models.Inventory;
import com.ecommerce.productcatalogservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Inventory> findByProduct(Product product);
    
    @Query("SELECT i FROM Inventory i WHERE i.lowStockAlert = true")
    List<Inventory> findAllLowStock();
    
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.minStockLevel")
    List<Inventory> findAllNeedingRestock();
} 