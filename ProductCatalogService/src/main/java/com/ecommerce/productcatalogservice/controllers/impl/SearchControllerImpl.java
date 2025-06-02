package com.ecommerce.productcatalogservice.controllers.impl;

import com.ecommerce.productcatalogservice.controllers.SearchController;
import com.ecommerce.productcatalogservice.dtos.SearchRequestDTO;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.services.impl.SearchService;
import com.ecommerce.commons.utils.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchControllerImpl implements SearchController {
    private final SearchService searchService;

    @Autowired
    public SearchControllerImpl(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/products")
    @Override
    public ResponseEntity<ApiResponse<Page<Product>>> searchProducts(@RequestBody SearchRequestDTO searchRequestDTO) {
        Page<Product> products = searchService.searchProducts(
                searchRequestDTO.getParamValue(),
                searchRequestDTO.getPageNumber(),
                searchRequestDTO.getPageSize(),
                searchRequestDTO.getSortParams()
        );
        return products.isEmpty() ? 
            ApiResponse.error(HttpStatus.NOT_FOUND, "No products found matching the search criteria") :
            ApiResponse.ok(products);
    }
}
