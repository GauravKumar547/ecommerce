package com.ecommerce.productcatalogservice.dtos;

import lombok.Data;

@Data
public class SortParams {
    private String field;
    private SortOrder order;
}
