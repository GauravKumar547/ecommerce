package com.ecommerce.productcatalogservice.models;


import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@Entity
@NoArgsConstructor
public class Product extends BaseModel {
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    @ManyToOne(cascade = CascadeType.ALL)
    private Category category;

}
