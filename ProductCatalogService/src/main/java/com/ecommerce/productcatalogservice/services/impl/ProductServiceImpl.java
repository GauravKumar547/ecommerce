package com.ecommerce.productcatalogservice.services.impl;

import com.ecommerce.productcatalogservice.dtos.BulkOperationResponse;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.repos.ProductRepository;
import com.ecommerce.productcatalogservice.services.ProductService;
import com.ecommerce.commons.exceptions.ResourceNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProductById(id);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());
        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Override
    @Cacheable(value = "products", key = "'page:' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Cacheable(value = "products", key = "'category:' + #categoryId")
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    @Transactional
    public BulkOperationResponse<Product> createProducts(List<Product> products) {
        BulkOperationResponse<Product> response = new BulkOperationResponse<>();
        response.setTotalCount(products.size());

        for (Product product : products) {
            try {
                Product savedProduct = productRepository.save(product);
                response.getSuccessItems().add(savedProduct);
                response.setSuccessCount(response.getSuccessCount() + 1);
            } catch (Exception e) {
                BulkOperationResponse.FailureItem failureItem = new BulkOperationResponse.FailureItem();
                failureItem.setId(product.getName());
                failureItem.setReason(e.getMessage());
                response.getFailureItems().add(failureItem);
                response.setFailureCount(response.getFailureCount() + 1);
            }
        }

        return response;
    }

    @Override
    @Transactional
    public BulkOperationResponse<Product> updateProducts(List<Product> products) {
        BulkOperationResponse<Product> response = new BulkOperationResponse<>();
        response.setTotalCount(products.size());

        for (Product product : products) {
            try {
                if (product.getId() == null) {
                    throw new IllegalArgumentException("Product ID cannot be null for update operation");
                }
                Product existingProduct = getProductById(product.getId());
                existingProduct.setName(product.getName());
                existingProduct.setDescription(product.getDescription());
                existingProduct.setPrice(product.getPrice());
                existingProduct.setCategory(product.getCategory());
                
                Product updatedProduct = productRepository.save(existingProduct);
                response.getSuccessItems().add(updatedProduct);
                response.setSuccessCount(response.getSuccessCount() + 1);
            } catch (Exception e) {
                BulkOperationResponse.FailureItem failureItem = new BulkOperationResponse.FailureItem();
                failureItem.setId(product.getId().toString());
                failureItem.setReason(e.getMessage());
                response.getFailureItems().add(failureItem);
                response.setFailureCount(response.getFailureCount() + 1);
            }
        }

        return response;
    }

    @Override
    @Transactional
    public BulkOperationResponse<Void> deleteProducts(List<Long> productIds) {
        BulkOperationResponse<Void> response = new BulkOperationResponse<>();
        response.setTotalCount(productIds.size());

        for (Long id : productIds) {
            try {
                if (!productRepository.existsById(id)) {
                    throw new ResourceNotFoundException("Product", "id", id);
                }
                productRepository.deleteById(id);
                response.setSuccessCount(response.getSuccessCount() + 1);
            } catch (Exception e) {
                BulkOperationResponse.FailureItem failureItem = new BulkOperationResponse.FailureItem();
                failureItem.setId(id.toString());
                failureItem.setReason(e.getMessage());
                response.getFailureItems().add(failureItem);
                response.setFailureCount(response.getFailureCount() + 1);
            }
        }

        return response;
    }
} 