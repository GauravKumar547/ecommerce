package com.ecommerce.productcatalogservice.dtos;

import com.ecommerce.productcatalogservice.models.State;
import lombok.Data;

@Data
public class ImageDTO {
    private long id;
    private String url;
    private State state = State.ACTIVE;
}
