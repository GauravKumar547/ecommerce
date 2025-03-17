package com.ecommerce.productcatalogservice.controllers.impl;

import com.ecommerce.productcatalogservice.controllers.ProductController;
import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import com.ecommerce.productcatalogservice.dtos.ResponseDTO;
import com.ecommerce.productcatalogservice.mappers.ImageMapper;
import com.ecommerce.productcatalogservice.mappers.ProductMapper;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.services.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.IllegalFormatWidthException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductControllerImpl implements ProductController {
    private final IProductService productService;

    @Autowired
    public ProductControllerImpl(@Qualifier("sqlProductService") IProductService productService) {
        this.productService = productService;
    }


    @Override
    @PostMapping
    public ProductDTO addProduct(@RequestBody ProductDTO product) {
        if(product == null) {
           throw new IllegalArgumentException("Product cannot be null");
        }else if(product.getCategory() == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        Product productResponse = productService.createProduct(ProductMapper.toProduct(product));
        return ProductMapper.toProductDTO(productResponse);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseDTO deleteProduct(@PathVariable long id) {
        if(id<1){
            throw new IllegalArgumentException("Product id must be greater than 0");
        }
        ResponseDTO responseDTO = new ResponseDTO();
        if(productService.deleteProductByID(id)){
            responseDTO.setMessage("Delete product successful");

        }else{
            responseDTO.setMessage("Delete product failed");
        }
        return responseDTO;
    }

    @Override
    @PutMapping("/{id}")
    public ProductDTO replaceProduct(@PathVariable long id, @RequestBody ProductDTO product) {
        if(id<1){
            throw new IllegalArgumentException("Product id must be greater than 0");
        } else if(product==null){
            throw new IllegalArgumentException("Product cannot be null");
        }
        Product productResponse = productService.replaceProductByID(id,ProductMapper.toProduct(product));

        return productResponse==null?null:ProductMapper.toProductDTO(productResponse);
    }

    @Override
    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable long id) {
        if(id<1){
            throw new IllegalArgumentException("Product id must be greater than 0");
        }
        Product product = productService.getProductByID(id);
        if (product == null) {
            return null;
        }
        return ProductMapper.toProductDTO(product);
    }

    @Override
    @GetMapping
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            return null;
        }
        return products.stream().map(ProductMapper::toProductDTO).toList();
    }

    @Override
    @GetMapping("category/{categoryName}")
    public List<ProductDTO> getProductsByCategory(@PathVariable String categoryName) {
        if(categoryName==null){
            throw new IllegalArgumentException("Category name cannot be null");
        }else if(categoryName.trim().isEmpty()){
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        return productService.getAllProducts().stream().filter(product-> product.getCategory() != null && product.getCategory().getName().equals(categoryName)).map(ProductMapper::toProductDTO).toList();
    }

    @Override
    @PatchMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable long id, @RequestBody ProductDTO product) {
        Product product1 = productService.getProductByID(id);
        if (product1 == null) {
            return null;
        }
        if(product.getName()!=null) {
            product1.setName(product.getName());
        }
        if(product.getPrice()>0) {
            product1.setPrice(product.getPrice());
        }
        if(product.getDescription()!=null) {
            product1.setDescription(product.getDescription());
        }
        if(!product.getImages().isEmpty()) {
            product1.setImages(product.getImages().stream().map(ImageMapper::
            toImage).collect(Collectors.toList()));
        }
        return ProductMapper.toProductDTO(productService.replaceProductByID(id, product1));
    }
}