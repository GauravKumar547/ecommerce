package com.ecommerce.productcatalogservice.controllers;

import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import com.ecommerce.productcatalogservice.dtos.ResponseDTO;

import java.util.List;

public interface ProductController {
    public ProductDTO addProduct(ProductDTO product);
    public ResponseDTO deleteProduct(long id);
    public ProductDTO replaceProduct(long id, ProductDTO product);
    public ProductDTO getProduct(long id);
    public List<ProductDTO> getAllProducts();
    public List<ProductDTO> getProductsByCategory(String categoryName);
    public ProductDTO updateProduct(long id, ProductDTO product);
}
