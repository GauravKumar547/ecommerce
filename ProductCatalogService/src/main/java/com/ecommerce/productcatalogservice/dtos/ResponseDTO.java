package com.ecommerce.productcatalogservice.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class ResponseDTO {
    private String message;
}