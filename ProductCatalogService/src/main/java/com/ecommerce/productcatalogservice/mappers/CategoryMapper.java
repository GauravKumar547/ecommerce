package com.ecommerce.productcatalogservice.mappers;

import com.ecommerce.productcatalogservice.dtos.CategoryDTO;
import com.ecommerce.productcatalogservice.models.Category;

public class CategoryMapper {
    static public CategoryDTO toCategoryDTO(Category category) {
        if(category == null){
            return null;
        }
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());
        return categoryDTO;
    }

    static public Category toCategory(CategoryDTO categoryDTO) {
        return categoryDTO!=null? Category.builder()
                .id(categoryDTO.getId())
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .build():null;
    }
}
