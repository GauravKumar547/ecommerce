package com.ecommerce.productcatalogservice.services;

import com.ecommerce.productcatalogservice.models.Category;
import com.ecommerce.productcatalogservice.models.Product;

import java.util.List;

public interface ICategoryService {
    Category getCategoryByID(long categoryId);
    List<Category> getAllCategories();
    Category replaceCategoryByID(long categoryId, Category category);
    Boolean deleteCategoryByID(long categoryId);
    Category createCategory(Category category);
}
