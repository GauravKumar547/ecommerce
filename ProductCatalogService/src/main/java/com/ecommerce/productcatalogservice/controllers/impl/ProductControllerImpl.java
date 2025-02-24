package com.ecommerce.productcatalogservice.controllers.impl;

import com.ecommerce.productcatalogservice.controllers.ProductController;
import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import com.ecommerce.productcatalogservice.dtos.ResponseDTO;
import com.ecommerce.productcatalogservice.mappers.ProductMapper;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.services.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class ProductControllerImpl implements ProductController {
    private final IProductService productService;

    public ProductControllerImpl(IProductService productService) {
        this.productService = productService;
    }


    @Override
    @PostMapping("/products/")
    public ProductDTO addProduct(@RequestBody ProductDTO product) {
        return null;
    }

    @Override
    @DeleteMapping("/products/{id}")
    public ResponseDTO deleteProduct(@PathVariable long id) {
        return null;
    }

    @Override
    @PutMapping("/products/{id}")
    public ProductDTO replaceProduct(@PathVariable long id, @RequestBody ProductDTO product) {
        Product productResponse = productService.replaceProductByID(id,ProductMapper.toProduct(product));

        return productResponse==null?null:ProductMapper.toProductDTO(productResponse);
    }

    @Override
    @GetMapping("/products/{id}")
    public ProductDTO getProduct(@PathVariable long id) {
        Product product = productService.getProductByID(id);
        if (product == null) {
            return null;
        }
        return ProductMapper.toProductDTO(product);
    }

    @Override
    @GetMapping("/products")
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            return null;
        }
        ArrayList<ProductDTO> productDTOs = new ArrayList<>(products.size());
        for(Product product : products) {
            productDTOs.add(ProductMapper.toProductDTO(product));
        }
        return productDTOs;
    }

    @Override
    @GetMapping("/products/category/{categoryId}")
    public List<ProductDTO> getProductsByCategory(@PathVariable long categoryId) {
        return List.of();
    }

    @Override
    @PatchMapping("/products/{id}")
    public ProductDTO updateProduct(@PathVariable long id, @RequestBody ProductDTO product) {
        return null;
    }
}
