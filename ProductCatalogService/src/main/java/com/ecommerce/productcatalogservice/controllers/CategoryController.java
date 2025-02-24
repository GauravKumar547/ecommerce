package com.ecommerce.productcatalogservice.controllers;

import com.ecommerce.productcatalogservice.dtos.CategoryDTO;
import com.ecommerce.productcatalogservice.dtos.ResponseDTO;

import java.util.List;

public interface CategoryController {
    public CategoryDTO getCategoryById(long id);
    public List<CategoryDTO> getAllCategories();
    public CategoryDTO createCategory(CategoryDTO categoryDTO);
    public CategoryDTO updateCategory(long id, CategoryDTO categoryDTO);
    public ResponseDTO deleteCategory(long id);
    public CategoryDTO updateCategoryFields(long id, CategoryDTO categoryDTO);
}
