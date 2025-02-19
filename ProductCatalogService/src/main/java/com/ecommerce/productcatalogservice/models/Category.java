package com.ecommerce.productcatalogservice.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class Category extends BaseModel {
    private String name;
    private String description;

}
