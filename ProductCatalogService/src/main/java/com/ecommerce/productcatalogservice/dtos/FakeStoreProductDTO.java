package com.ecommerce.productcatalogservice.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FakeStoreProductDTO {
    private long id;
    private String title;
    private double price;
    private String description;
    private String image;
    private String category;
}
