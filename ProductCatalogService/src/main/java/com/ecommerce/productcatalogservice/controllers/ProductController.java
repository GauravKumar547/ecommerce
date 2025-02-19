package com.ecommerce.productcatalogservice.controllers;

import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import com.ecommerce.productcatalogservice.dtos.ResponsDTO;

import java.util.List;

public interface ProductController {
    public ProductDTO addProduct(ProductDTO product);
    public ResponsDTO deleteProduct(long id);
    public ProductDTO updateProduct(long id, ProductDTO product);
    public ProductDTO getProduct(long id);
    public List<ProductDTO> getProductsByCategory(long categoryID);
    public ProductDTO updateProductFields(long id, ProductDTO product);
}
