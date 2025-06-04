package com.ecommerce.productcatalogservice.controllers.impl;

import com.ecommerce.productcatalogservice.controllers.ProductController;
import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import com.ecommerce.productcatalogservice.dtos.ResponseDTO;
import com.ecommerce.productcatalogservice.mappers.ProductMapper;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.services.IProductService;
import com.ecommerce.commons.utils.response.ApiResponse;
import com.ecommerce.commons.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product Management", description = "APIs for managing products")
@SecurityRequirement(name = "JWT")
public class ProductControllerImpl implements ProductController {
    private final IProductService productService;

    @Autowired
    public ProductControllerImpl(@Qualifier("sqlProductService") IProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(
        summary = "Add product", 
        description = "Add a new product. Requires authentication with ADMIN role."
    )
    @Override
    public ResponseEntity<ApiResponse<ProductDTO>> addProduct(@RequestBody ProductDTO productDTO) {
        Product product = ProductMapper.toProduct(productDTO);
        Product savedProduct = productService.createProduct(product);
        return ApiResponse.created(ProductMapper.toProductDTO(savedProduct));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete product", 
        description = "Delete a product by ID. Requires authentication with ADMIN role."
    )
    @Override
    public ResponseEntity<ApiResponse<ResponseDTO>> deleteProduct(@PathVariable long id) {
        if (productService.deleteProductByID(id)) {
            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setMessage("Product deleted successfully");
            return ApiResponse.ok(responseDTO);
        }
        throw new ResourceNotFoundException("Product", "id", id);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Replace product", 
        description = "Replace a product by ID. Requires authentication with ADMIN role."
    )
    @Override
    public ResponseEntity<ApiResponse<ProductDTO>> replaceProduct(@PathVariable long id, @RequestBody ProductDTO productDTO) {
        Product product = ProductMapper.toProduct(productDTO);
        Product updatedProduct = productService.replaceProductByID(id, product);
        if (updatedProduct != null) {
            return ApiResponse.ok(ProductMapper.toProductDTO(updatedProduct));
        }
        throw new ResourceNotFoundException("Product", "id", id);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get product by ID", 
        description = "Get a product by its ID. Requires authentication."
    )
    @Override
    public ResponseEntity<ApiResponse<ProductDTO>> getProduct(@PathVariable long id) {
        Product product = productService.getProductByID(id);
        if (product != null) {
            return ApiResponse.ok(ProductMapper.toProductDTO(product));
        }
        throw new ResourceNotFoundException("Product", "id", id);
    }

    @GetMapping
    @Operation(
        summary = "Get all products", 
        description = "Get all products. Requires authentication."
    )
    @Override
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDTO> productDTOs = products.stream()
                .map(ProductMapper::toProductDTO)
                .collect(Collectors.toList());
        return ApiResponse.ok(productDTOs);
    }

    @GetMapping("/category/{categoryName}")
    @Operation(
        summary = "Get products by category", 
        description = "Get all products in a category. Requires authentication."
    )
    @Override
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByCategory(@PathVariable String categoryName) {
        List<Product> products = productService.getAllProducts().stream()
                .filter(product -> product.getCategory().getName().equals(categoryName))
                .collect(Collectors.toList());
        List<ProductDTO> productDTOs = products.stream()
                .map(ProductMapper::toProductDTO)
                .collect(Collectors.toList());
        return ApiResponse.ok(productDTOs);
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Update product", 
        description = "Partially update a product. Requires authentication with ADMIN role."
    )
    @Override
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@PathVariable long id, @RequestBody ProductDTO productDTO) {
        Product product = ProductMapper.toProduct(productDTO);
        Product updatedProduct = productService.replaceProductByID(id, product);
        if (updatedProduct != null) {
            return ApiResponse.ok(ProductMapper.toProductDTO(updatedProduct));
        }
        throw new ResourceNotFoundException("Product", "id", id);
    }

    @GetMapping("/{productId}/user/{userId}")
    @Operation(
        summary = "Get product by user scope", 
        description = "Get a product considering user's permissions. Requires authentication."
    )
    @Override
    public ResponseEntity<ApiResponse<ProductDTO>> getProductByUserScope(@PathVariable Long productId, @PathVariable Long userId) {
        Product product = productService.getProductByUserScope(productId, userId);
        if (product != null) {
            return ApiResponse.ok(ProductMapper.toProductDTO(product));
        }
        throw new ResourceNotFoundException("Product", "id", productId);
    }
}