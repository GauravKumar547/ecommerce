package com.ecommerce.productcatalogservice.controllers;

import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import com.ecommerce.productcatalogservice.dtos.ResponseDTO;
import com.ecommerce.productcatalogservice.utils.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductController {
    public ResponseEntity<ApiResponse<ProductDTO>> addProduct(ProductDTO product);
    public ResponseEntity<ApiResponse<ResponseDTO>> deleteProduct(long id);
    public ResponseEntity<ApiResponse<ProductDTO>> replaceProduct(long id, ProductDTO product);
    public ResponseEntity<ApiResponse<ProductDTO>> getProduct(long id);
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts();
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByCategory(String categoryName);
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(long id, ProductDTO product);
    public ResponseEntity<ApiResponse<ProductDTO>> getProductByUserScope(Long productId, Long userId);
}