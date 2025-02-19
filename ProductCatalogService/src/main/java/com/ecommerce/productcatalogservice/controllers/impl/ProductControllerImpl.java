package com.ecommerce.productcatalogservice.controllers.impl;

import com.ecommerce.productcatalogservice.controllers.ProductController;
import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import com.ecommerce.productcatalogservice.dtos.ResponsDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductControllerImpl implements ProductController {
    @Override
    @PostMapping("/products/")
    public ProductDTO addProduct(@RequestBody ProductDTO product) {
        return null;
    }

    @Override
    @DeleteMapping("/products/{id}")
    public ResponsDTO deleteProduct(@PathVariable long id) {
        return null;
    }

    @Override
    @PutMapping("/products/{id}")
    public ProductDTO updateProduct(@PathVariable long id, @RequestBody ProductDTO product) {
        return null;
    }

    @Override
    @GetMapping("/products/{id}")
    public ProductDTO getProduct(@PathVariable long id) {
        return null;
    }

    @Override
    @GetMapping("/products/category/{categoryId}")
    public List<ProductDTO> getProductsByCategory(@PathVariable long categoryId) {
        return List.of();
    }

    @Override
    @PatchMapping("/products/{id}")
    public ProductDTO updateProductFields(@PathVariable long id, @RequestBody ProductDTO product) {
        return null;
    }
}
