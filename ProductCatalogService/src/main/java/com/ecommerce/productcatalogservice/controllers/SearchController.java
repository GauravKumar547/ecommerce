package com.ecommerce.productcatalogservice.controllers;


import com.ecommerce.productcatalogservice.dtos.SearchRequestDTO;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.utils.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface SearchController {
    ResponseEntity<ApiResponse<Page<Product>>> searchProducts(SearchRequestDTO searchRequestDTO);
}
