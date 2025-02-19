package com.ecommerce.productcatalogservice.models;


import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class Product extends BaseModel {
    private String name;
    private String description;
    private double price;
    private List<String> images;
    private Category category;

}
