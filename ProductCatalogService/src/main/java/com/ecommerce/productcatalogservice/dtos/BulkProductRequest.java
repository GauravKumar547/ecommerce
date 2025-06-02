package com.ecommerce.productcatalogservice.dtos;

import com.ecommerce.productcatalogservice.models.Product;
import lombok.Data;

import java.util.List;

@Data
public class BulkProductRequest {
    private List<Product> products;
} 