package com.ecommerce.productcatalogservice.controllers;

import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.services.IProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ProductControllerTest {
    @MockitoBean
    @Qualifier("sqlProductService")
    private IProductService productService;

    @Autowired
    private ProductController productController;

    @Test
    public void TestGetProduct_WithValidId_RunsSuccessfully() {
        // Arrange
        long id = 3L;
        Product product = new Product();
        product.setId(id);
        when(productService.getProductByID(id)).thenReturn(product);

        // Act
        ProductDTO productDTO = productController.getProduct(id);

        // Assert
        assertNotNull(productDTO);
        assertEquals(id, productDTO.getId());
    }

    @Test
    public void TestGetProduct_WithNegativeId_ThrowsIllegalArgumentException() {
        // Arrange
        long id = -1L;

        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->productController.getProduct(id));
        assertEquals("Product id must be greater than 0", exception.getMessage());
    }
}
