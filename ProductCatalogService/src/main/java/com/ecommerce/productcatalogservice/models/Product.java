package com.ecommerce.productcatalogservice.models;


import java.util.List;

public class Product extends BaseModel {
    private String name;
    private String description;
    private double price;
    private List<String> images;
    private Category category;
}
