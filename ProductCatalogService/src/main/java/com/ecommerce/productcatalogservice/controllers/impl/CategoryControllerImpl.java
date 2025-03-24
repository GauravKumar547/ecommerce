package com.ecommerce.productcatalogservice.controllers.impl;

import com.ecommerce.productcatalogservice.controllers.CategoryController;
import com.ecommerce.productcatalogservice.dtos.CategoryDTO;
import com.ecommerce.productcatalogservice.dtos.ResponseDTO;
import com.ecommerce.productcatalogservice.mappers.CategoryMapper;
import com.ecommerce.productcatalogservice.models.Category;
import com.ecommerce.productcatalogservice.services.impl.CategoryService;
import com.ecommerce.productcatalogservice.utils.response.ApiResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryControllerImpl implements CategoryController {
    CategoryService categoryService;
    public CategoryControllerImpl(@Qualifier("sqlCategoryService") CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryById(@PathVariable long id) {
        if(id<1){
            throw new IllegalArgumentException("Category id cannot be less than 1");
        }
        Category category = categoryService.getCategoryByID(id);
        ApiResponse<CategoryDTO> apiResponse = new ApiResponse<>();
        if(category==null){
            apiResponse.setError("Category not found").setStatus(HttpStatus.NOT_FOUND);
        }else{
            apiResponse.setData(CategoryMapper.toCategoryDTO(category)).setStatus(HttpStatus.OK);
        }
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories() {
        ApiResponse<List<CategoryDTO>> apiResponse = new ApiResponse<>();
        List<Category>  categories = categoryService.getAllCategories();
        List<CategoryDTO> categoryDTOS = categories.stream().map(CategoryMapper::toCategoryDTO).toList();
        apiResponse.setData(categoryDTOS).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@RequestBody CategoryDTO categoryDTO) {
        if(categoryDTO==null){
            throw new IllegalArgumentException("Category cannot be null");
        }else if(categoryDTO.getName()==null || categoryDTO.getName().trim().isEmpty()){
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        ApiResponse<CategoryDTO> apiResponse = new ApiResponse<>();
        Category category = categoryService.createCategory(CategoryMapper.toCategory(categoryDTO));
        CategoryDTO categoryDTOResponse = CategoryMapper.toCategoryDTO(category);
        apiResponse.setData(categoryDTOResponse).setStatus(HttpStatus.CREATED);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> replaceCategory(@PathVariable long id,
                                                                    @RequestBody CategoryDTO categoryDTO) {
        if(id<1){
            throw new IllegalArgumentException("Category id cannot be less than 1");
        }else if(categoryDTO==null){
            throw new IllegalArgumentException("Category cannot be null");
        }
        ApiResponse<CategoryDTO> apiResponse = new ApiResponse<>();
        Category category =categoryService.replaceCategoryByID(id,CategoryMapper.toCategory(categoryDTO));
        CategoryDTO categoryDTOResponse = CategoryMapper.toCategoryDTO(category);
        apiResponse.setData(categoryDTOResponse).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ResponseDTO>> deleteCategory(@PathVariable long id) {
        ResponseDTO responseDTO = new ResponseDTO();
        ApiResponse<ResponseDTO> apiResponse = new ApiResponse<>();
        if(categoryService.deleteCategoryByID(id)){
            responseDTO.setMessage("Successfully deleted Category");
            apiResponse.setData(responseDTO).setStatus(HttpStatus.OK);
        }else{
            responseDTO.setMessage("Failed to delete Category");
            apiResponse.setData(responseDTO).setStatus(HttpStatus.NOT_FOUND);
        }
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategoryFields(@PathVariable long id, @RequestBody CategoryDTO categoryDTO) {
        ApiResponse<CategoryDTO> apiResponse = new ApiResponse<>();
        Category category = categoryService.getCategoryByID(id);
        if(category==null){
            apiResponse.setError("Category not found to update").setStatus(HttpStatus.NOT_FOUND);
            return ApiResponse.getResponseEntity(apiResponse);
        }
        if(categoryDTO.getName()!=null){
            category.setName(categoryDTO.getName());
        }
        if(categoryDTO.getDescription()!=null){
            category.setDescription(categoryDTO.getDescription());
        }
        Category categoryResponse = categoryService.replaceCategoryByID(id, category);
        apiResponse.setData(CategoryMapper.toCategoryDTO(categoryResponse)).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }
}