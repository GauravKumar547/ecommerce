package com.ecommerce.productcatalogservice.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {
    private long id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private CategoryDTO category;
}
