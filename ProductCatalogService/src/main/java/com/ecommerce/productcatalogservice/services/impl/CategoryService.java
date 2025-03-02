package com.ecommerce.productcatalogservice.services.impl;

import com.ecommerce.productcatalogservice.models.Category;
import com.ecommerce.productcatalogservice.models.State;
import com.ecommerce.productcatalogservice.repos.CategoryRepository;
import com.ecommerce.productcatalogservice.services.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("sqlCategoryService")
public class CategoryService implements ICategoryService {
    CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category getCategoryByID(long categoryId) {
        return categoryRepository.findById(categoryId).orElse(null);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByState(State.ACTIVE);
    }

    @Override
    public Category replaceCategoryByID(long categoryId, Category category) {
        if(categoryRepository.existsById(categoryId)) {
            category.setId(categoryId);
            return categoryRepository.save(category);
        }
        return null;
    }

    @Override
    public Boolean deleteCategoryByID(long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if(optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setState(State.DELETED);
            categoryRepository.save(category);
            return true;
        }
        return false;
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
}
