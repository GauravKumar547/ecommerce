package com.ecommerce.productcatalogservice.services;

import com.ecommerce.productcatalogservice.models.Product;

import java.util.List;

public interface IProductService {
    Product getProductByID(long productId);
    List<Product> getAllProducts();
    Product replaceProductByID(long productId, Product product);
    Boolean deleteProductByID(long productId);
    Product createProduct(Product product);
}
