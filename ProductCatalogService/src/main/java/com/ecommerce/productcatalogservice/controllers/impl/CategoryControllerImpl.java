package com.ecommerce.productcatalogservice.controllers.impl;

import com.ecommerce.productcatalogservice.controllers.CategoryController;
import com.ecommerce.productcatalogservice.dtos.CategoryDTO;
import com.ecommerce.productcatalogservice.dtos.ResponseDTO;
import com.ecommerce.productcatalogservice.mappers.CategoryMapper;
import com.ecommerce.productcatalogservice.models.Category;
import com.ecommerce.productcatalogservice.services.impl.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CategoryControllerImpl implements CategoryController {
    CategoryService categoryService;
    public CategoryControllerImpl(@Qualifier("sqlCategoryService") CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @Override
    public CategoryDTO getCategoryById(long id) {
        if(id<1){
            throw new IllegalArgumentException("Category id cannot be less than 1");
        }
        Category category = categoryService.getCategoryByID(id);
        return category==null?null:  CategoryMapper.toCategoryDTO(category);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories().stream().map(CategoryMapper::toCategoryDTO).collect(Collectors.toList());
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if(categoryDTO==null){
            throw new IllegalArgumentException("Category cannot be null");
        }else if(categoryDTO.getName()==null || categoryDTO.getName().trim().isEmpty()){
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        return CategoryMapper.toCategoryDTO(categoryService.createCategory(CategoryMapper.toCategory(categoryDTO)));
    }

    @Override
    public CategoryDTO replaceCategory(long id, CategoryDTO categoryDTO) {
        if(id<1){
            throw new IllegalArgumentException("Category id cannot be less than 1");
        }else if(categoryDTO==null){
            throw new IllegalArgumentException("Category cannot be null");
        }
        return CategoryMapper.toCategoryDTO(categoryService.replaceCategoryByID(id, CategoryMapper.toCategory(categoryDTO)));
    }

    @Override
    public ResponseDTO deleteCategory(long id) {
        ResponseDTO responseDTO = new ResponseDTO();
        if(categoryService.deleteCategoryByID(id)){
            responseDTO.setMessage("Successfully deleted Category");
        }else{
            responseDTO.setMessage("Failed to delete Category");
        }
        return responseDTO;
    }

    @Override
    public CategoryDTO updateCategoryFields(long id, CategoryDTO categoryDTO) {
        Category category = categoryService.getCategoryByID(id);
        if(category==null){
            return null;
        }
        if(categoryDTO.getName()!=null){
            category.setName(categoryDTO.getName());
        }
        if(categoryDTO.getDescription()!=null){
            category.setDescription(categoryDTO.getDescription());
        }
        return CategoryMapper.toCategoryDTO(categoryService.replaceCategoryByID(id, category));
    }
}