package com.ecommerce.productcatalogservice.models;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@SuperBuilder
@Entity
@NoArgsConstructor
public class Product extends BaseModel {
    private String name;
    private String description;
    private double price;
    @ManyToMany(mappedBy = "products", cascade = CascadeType.DETACH)
    private List<Image> images;
    @ManyToOne(cascade = CascadeType.ALL)
    private Category category;

}
