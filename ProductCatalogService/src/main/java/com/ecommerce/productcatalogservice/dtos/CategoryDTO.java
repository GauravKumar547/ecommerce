package com.ecommerce.productcatalogservice.dtos;

import com.ecommerce.productcatalogservice.models.State;
import lombok.Data;

@Data
public class CategoryDTO {
    private long id;
    private String name;
    private String description;
    private State state = State.ACTIVE;
}
