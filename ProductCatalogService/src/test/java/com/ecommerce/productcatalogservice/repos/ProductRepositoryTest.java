package com.ecommerce.productcatalogservice.repos;

import com.ecommerce.productcatalogservice.models.Category;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.models.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTest {
    @Autowired
    ProductRepository productRepository;

    @Test
    public void TestFindAllByState_RunsSuccessfully() {
        // Act
        List<Product> products = productRepository.findAll();
        List<Product> activeProducts = productRepository.findAllByState(State.ACTIVE);
        List<Product> deletedProducts = productRepository.findAllByState(State.DELETED);

        // Assert
        assertEquals(products.stream().filter(product -> State.ACTIVE.equals(product.getState())).toList().size(),
                activeProducts.size());
        assertEquals(products.stream().filter(product -> State.DELETED.equals(product.getState())).toList().size(),
                deletedProducts.size());

    }

    @Test
    public void TestFindAllByCategoryName_RunsSuccessfully() {
        //  Arrange
        Category category = Category.builder().id(1L).name("Men's fashion").build();
        // Act
        List<Product> products = productRepository.findAll();
        List<Product> categoryProducts = productRepository.findAllByCategory(category);

        // Assert
        assertEquals(products.stream().filter(product -> category.getId() == product.getCategory().getId()).toList().size(),
                categoryProducts.size());

    }
}