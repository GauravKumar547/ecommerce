package com.ecommerce.productcatalogservice.controllers;

import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import com.ecommerce.productcatalogservice.dtos.ResponsDTO;

import java.util.List;

public interface ProductController {
    public ProductDTO addProduct(ProductDTO product);
    public ResponsDTO deleteProduct(ProductDTO product);
    public ProductDTO updateProduct(ProductDTO product);
    public ProductDTO getProduct(long id);
    public List<ProductDTO> getProductsByCategory(String category);
    public ProductDTO updateProductFields(ProductDTO product);
}
