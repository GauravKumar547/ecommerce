package com.ecommerce.productcatalogservice.controllers;

import com.ecommerce.productcatalogservice.dtos.CategoryDTO;
import com.ecommerce.productcatalogservice.dtos.ResponsDTO;

import java.util.List;

public interface CategoryController {
    public CategoryDTO getCategoryById(long id);
    public List<CategoryDTO> getAllCategories();
    public CategoryDTO createCategory(CategoryDTO categoryDTO);
    public CategoryDTO updateCategory(CategoryDTO categoryDTO);
    public ResponsDTO deleteCategory(long id);
    public CategoryDTO updateCategoryFields(CategoryDTO categoryDTO);
}
