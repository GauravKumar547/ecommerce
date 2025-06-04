package com.ecommerce.productcatalogservice.controllers;

import com.ecommerce.productcatalogservice.models.Inventory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


public interface InventoryController {

    public ResponseEntity<Inventory> createInventory(@RequestBody Inventory inventory);

    public ResponseEntity<Inventory> updateInventory(@PathVariable Long id, @RequestBody Inventory inventory);

    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) ;

    public ResponseEntity<Inventory> getInventory(@PathVariable Long id) ;

    public ResponseEntity<List<Inventory>> getAllInventory() ;

    public ResponseEntity<List<Inventory>> getLowStockItems() ;

    public ResponseEntity<List<Inventory>> getItemsNeedingRestock() ;

    public ResponseEntity<Boolean> reserveStock(@PathVariable Long productId, @PathVariable Integer quantity) ;

    public ResponseEntity<Boolean> releaseStock(@PathVariable Long productId, @PathVariable Integer quantity) ;

    public ResponseEntity<Boolean> confirmStockReservation(@PathVariable Long productId, @PathVariable Integer quantity);
} 