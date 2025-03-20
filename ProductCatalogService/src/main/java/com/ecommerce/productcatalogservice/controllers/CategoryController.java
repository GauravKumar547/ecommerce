package com.ecommerce.productcatalogservice.controllers;

import com.ecommerce.productcatalogservice.dtos.CategoryDTO;
import com.ecommerce.productcatalogservice.dtos.ResponseDTO;
import com.ecommerce.productcatalogservice.utils.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryController {
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryById(long id);
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories();
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(CategoryDTO categoryDTO);
    public ResponseEntity<ApiResponse<CategoryDTO>> replaceCategory(long id, CategoryDTO categoryDTO);
    public ResponseEntity<ApiResponse<ResponseDTO>> deleteCategory(long id);
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategoryFields(long id, CategoryDTO categoryDTO);
}