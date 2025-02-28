package com.ecommerce.productcatalogservice.repos;

import com.ecommerce.productcatalogservice.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
