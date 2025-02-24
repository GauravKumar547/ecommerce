package com.ecommerce.productcatalogservice.controllers.impl;

import com.ecommerce.productcatalogservice.controllers.CategoryController;
import com.ecommerce.productcatalogservice.dtos.CategoryDTO;
import com.ecommerce.productcatalogservice.dtos.ResponseDTO;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryControllerImpl implements CategoryController {
    @Override
    public CategoryDTO getCategoryById(long id) {
        return null;
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return List.of();
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        return null;
    }

    @Override
    public CategoryDTO updateCategory(long id, CategoryDTO categoryDTO) {
        return null;
    }

    @Override
    public ResponseDTO deleteCategory(long id) {
        return null;
    }

    @Override
    public CategoryDTO updateCategoryFields(long id, CategoryDTO categoryDTO) {
        return null;
    }
}
