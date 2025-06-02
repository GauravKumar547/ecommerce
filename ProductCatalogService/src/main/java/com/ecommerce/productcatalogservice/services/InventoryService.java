package com.ecommerce.productcatalogservice.services;

import com.ecommerce.productcatalogservice.models.Inventory;
import java.util.List;

public interface InventoryService {
    Inventory createInventory(Inventory inventory);
    Inventory updateInventory(Long id, Inventory inventory);
    void deleteInventory(Long id);
    Inventory getInventoryById(Long id);
    List<Inventory> getAllInventory();
    List<Inventory> getLowStockItems();
    List<Inventory> getItemsNeedingRestock();
    boolean reserveStock(Long productId, Integer quantity);
    boolean releaseStock(Long productId, Integer quantity);
    boolean confirmStockReservation(Long productId, Integer quantity);
} 