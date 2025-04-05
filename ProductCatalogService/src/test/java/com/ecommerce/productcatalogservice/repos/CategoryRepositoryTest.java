package com.ecommerce.productcatalogservice.repos;


import com.ecommerce.productcatalogservice.models.Category;
import com.ecommerce.productcatalogservice.models.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use actual MySQL instead of an embedded DB
public class CategoryRepositoryTest {
    @Autowired
    CategoryRepository categoryRepository;

    @Test
    public void TestFindAllByState_RunsSuccessfully() {
        // Act
        List<Category> categories = categoryRepository.findAll();
        List<Category> activeCategories = categoryRepository.findAllByState(State.ACTIVE);
        List<Category> deletedCategories = categoryRepository.findAllByState(State.DELETED);

        // Assert
        assertEquals(categories.stream().filter(category -> State.ACTIVE.equals(category.getState())).toList().size(),
                activeCategories.size());
        assertEquals(categories.stream().filter(category -> State.DELETED.equals(category.getState())).toList().size(),
                deletedCategories.size());

    }
}