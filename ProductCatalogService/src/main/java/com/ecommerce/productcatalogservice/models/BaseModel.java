package com.ecommerce.productcatalogservice.models;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@SuperBuilder
public abstract class BaseModel {
    private long id;
    private Date createdAt;
    private Date updatedAt;
    private State state = State.ACTIVE;
}
