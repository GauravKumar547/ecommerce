package com.ecommerce.productcatalogservice.services.impl;

import com.ecommerce.productcatalogservice.dtos.SortOrder;
import com.ecommerce.productcatalogservice.dtos.SortParams;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.repos.ProductRepository;
import com.ecommerce.productcatalogservice.services.ISearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService implements ISearchService {
    private final ProductRepository productRepository;

    @Autowired
    public SearchService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Override
    public Page<Product> searchProducts(String name, int pageNumber, int pageSize, List<SortParams> sortParamsList) {
        Sort sort = Sort.by("id");
        if (!sortParamsList.isEmpty()) {
            if(sortParamsList.getFirst().getOrder().equals(SortOrder.ASC)) {
                sort = Sort.by(sortParamsList.getFirst().getField());
            }else{
                sort = Sort.by(sortParamsList.getFirst().getField()).descending();
            }
        }
        for (int i = 1; i < sortParamsList.size(); i++) {
            if(sortParamsList.get(i).getOrder().equals(SortOrder.ASC)) {
                sort.and( Sort.by(sortParamsList.get(i).getField()));
            }else{
                sort.and(Sort.by(sortParamsList.get(i).getField()).descending());
            }
        }
        System.out.println("name: "+name);
        return productRepository.findAllByCategoryName(name, PageRequest.of(pageNumber, pageSize, sort));
    }
}
