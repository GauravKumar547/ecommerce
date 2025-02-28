package com.ecommerce.productcatalogservice.services.impl;

import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.services.IProductService;

import java.util.List;

public class StorgageProductService implements IProductService {

    @Override
    public Product getProductByID(long productId) {
        return null;
    }

    @Override
    public List<Product> getAllProducts() {
        return List.of();
    }

    @Override
    public Product replaceProductByID(long productId, Product product) {
        return null;
    }

    @Override
    public Boolean deleteProductByID(long productId) {
        return null;
    }

    @Override
    public Product createProduct(Product product) {
        return null;
    }
}
