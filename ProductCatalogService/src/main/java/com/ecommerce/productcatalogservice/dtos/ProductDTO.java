package com.ecommerce.productcatalogservice.dtos;

import com.ecommerce.productcatalogservice.models.State;
import lombok.Data;

import java.util.List;

@Data
public class ProductDTO{
    private long id;
    private String name;
    private String description;
    private double price;
    private List<ImageDTO> images;
    private CategoryDTO category;
    private State state = State.ACTIVE;
}