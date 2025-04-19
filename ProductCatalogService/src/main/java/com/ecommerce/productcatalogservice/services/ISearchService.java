package com.ecommerce.productcatalogservice.services;

import com.ecommerce.productcatalogservice.dtos.SortParams;
import com.ecommerce.productcatalogservice.models.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ISearchService {
    Page<Product> searchProducts(String name, int pageNumber, int pageSize, List<SortParams> sortParamsList);
}
