package com.ecommerce.productcatalogservice.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchRequestDTO {
    private String paramValue;
    private int pageNumber;
    private int pageSize;
    private List<SortParams> sortParams = new ArrayList<>();
}
