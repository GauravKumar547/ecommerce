package com.ecommerce.productcatalogservice.services;

import com.ecommerce.productcatalogservice.dtos.BulkOperationResponse;
import com.ecommerce.productcatalogservice.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    Product getProductById(Long id);
    Page<Product> getAllProducts(Pageable pageable);
    List<Product> getProductsByCategory(Long categoryId);
    BulkOperationResponse<Product> createProducts(List<Product> products);
    BulkOperationResponse<Product> updateProducts(List<Product> products);
    BulkOperationResponse<Void> deleteProducts(List<Long> productIds);
} 