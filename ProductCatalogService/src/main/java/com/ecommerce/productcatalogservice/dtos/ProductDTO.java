package com.ecommerce.productcatalogservice.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {
    private long id;
    private String name;
    private String description;
    private double price;
    private List<String> images;
    private CategoryDTO category;
}
