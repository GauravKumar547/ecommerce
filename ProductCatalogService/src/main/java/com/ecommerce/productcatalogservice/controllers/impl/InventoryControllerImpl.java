package com.ecommerce.productcatalogservice.controllers.impl;

import com.ecommerce.productcatalogservice.controllers.InventoryController;
import com.ecommerce.productcatalogservice.models.Inventory;
import com.ecommerce.productcatalogservice.services.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory Management", description = "APIs for managing product inventory")
public class InventoryControllerImpl implements InventoryController {

    private final InventoryService inventoryService;

    public InventoryControllerImpl(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @Operation(summary = "Create inventory", description = "Creates a new inventory record for a product")
    public ResponseEntity<Inventory> createInventory(@RequestBody Inventory inventory) {
        return ResponseEntity.ok(inventoryService.createInventory(inventory));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update inventory", description = "Updates an existing inventory record")
    public ResponseEntity<Inventory> updateInventory(@PathVariable Long id, @RequestBody Inventory inventory) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, inventory));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete inventory", description = "Deletes an inventory record")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get inventory by ID", description = "Retrieves an inventory record by its ID")
    public ResponseEntity<Inventory> getInventory(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @GetMapping
    @Operation(summary = "Get all inventory", description = "Retrieves all inventory records")
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock items", description = "Retrieves all items with low stock")
    public ResponseEntity<List<Inventory>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }

    @GetMapping("/needs-restock")
    @Operation(summary = "Get items needing restock", description = "Retrieves all items that need restocking")
    public ResponseEntity<List<Inventory>> getItemsNeedingRestock() {
        return ResponseEntity.ok(inventoryService.getItemsNeedingRestock());
    }

    @PostMapping("/{productId}/reserve/{quantity}")
    @Operation(summary = "Reserve stock", description = "Reserves a specific quantity of a product")
    public ResponseEntity<Boolean> reserveStock(@PathVariable Long productId, @PathVariable Integer quantity) {
        return ResponseEntity.ok(inventoryService.reserveStock(productId, quantity));
    }

    @PostMapping("/{productId}/release/{quantity}")
    @Operation(summary = "Release stock", description = "Releases a previously reserved quantity of a product")
    public ResponseEntity<Boolean> releaseStock(@PathVariable Long productId, @PathVariable Integer quantity) {
        return ResponseEntity.ok(inventoryService.releaseStock(productId, quantity));
    }

    @PostMapping("/{productId}/confirm/{quantity}")
    @Operation(summary = "Confirm stock reservation", description = "Confirms a stock reservation and updates the inventory")
    public ResponseEntity<Boolean> confirmStockReservation(@PathVariable Long productId, @PathVariable Integer quantity) {
        return ResponseEntity.ok(inventoryService.confirmStockReservation(productId, quantity));
    }
} 