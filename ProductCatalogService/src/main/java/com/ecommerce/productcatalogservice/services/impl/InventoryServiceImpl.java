package com.ecommerce.productcatalogservice.services.impl;

import com.ecommerce.productcatalogservice.models.Inventory;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.repos.InventoryRepository;
import com.ecommerce.productcatalogservice.repos.ProductRepository;
import com.ecommerce.productcatalogservice.services.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Inventory createInventory(Inventory inventory) {
        inventory.updateLowStockAlert();
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory updateInventory(Long id, Inventory inventory) {
        Inventory existingInventory = getInventoryById(id);
        existingInventory.setQuantity(inventory.getQuantity());
        existingInventory.setMinStockLevel(inventory.getMinStockLevel());
        existingInventory.setMaxStockLevel(inventory.getMaxStockLevel());
        existingInventory.updateLowStockAlert();
        return inventoryRepository.save(existingInventory);
    }

    @Override
    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }

    @Override
    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found with id: " + id));
    }

    @Override
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findAllLowStock();
    }

    @Override
    public List<Inventory> getItemsNeedingRestock() {
        return inventoryRepository.findAllNeedingRestock();
    }

    @Override
    @Transactional
    public boolean reserveStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        
        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getAvailableQuantity() >= quantity) {
            inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
            inventoryRepository.save(inventory);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean releaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        
        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getReservedQuantity() >= quantity) {
            inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
            inventoryRepository.save(inventory);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean confirmStockReservation(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        
        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getReservedQuantity() >= quantity) {
            inventory.setQuantity(inventory.getQuantity() - quantity);
            inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
            inventory.updateLowStockAlert();
            inventoryRepository.save(inventory);
            return true;
        }
        return false;
    }
} 