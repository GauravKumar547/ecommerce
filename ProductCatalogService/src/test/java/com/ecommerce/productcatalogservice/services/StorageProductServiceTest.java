package com.ecommerce.productcatalogservice.services;

import com.ecommerce.productcatalogservice.models.Category;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.models.State;
import com.ecommerce.productcatalogservice.repos.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class StorageProductServiceTest {

    @MockitoBean
    private ProductRepository productRepository;

    @Autowired
    @Qualifier("sqlProductService")
    private IProductService productService;

    @Captor
    private ArgumentCaptor<Long> idCaptor;
    @Test
    public void TestGetProductById_WithValidId_RunsSuccessfully() {
        // Arrange
        long productId = 1L;
        Product product = Product.builder().id(productId).name("Test Product").description("Test Description").build();
        Optional<Product> categoryOptional = Optional.of(product);
        when(productRepository.findById(anyLong())).thenReturn(categoryOptional);
        // Act
        Product fetchedProduct= productService.getProductByID(productId);
        // Assert
        assertNotNull(fetchedProduct);
        assertEquals(productId, fetchedProduct.getId());
        assertEquals("Test Product", fetchedProduct.getName());
        assertEquals("Test Description", fetchedProduct.getDescription());
    }

    @Test
    public void TestGetProductById_WithInvalidId_ReturnsNull() {
        // Act and Assert
        long productId = 5L;
        Optional<Product> categoryOptional = Optional.empty();
        when(productRepository.findById(anyLong())).thenReturn(categoryOptional);
        // Act
        Product fetchedProduct= productService.getProductByID(productId);
        assertNull(fetchedProduct);
    }

    @Test
    public void TestGetAllCategories_RunsSuccessfully(){
        // Arrange
        when(productRepository.findAllByState(any(State.class))).thenReturn(List.of(new Product(), new Product(),
                new Product()));

        // Act
        List<Product> categoryList = productService.getAllProducts();

        // Assert
        assertNotNull(categoryList);
        assertEquals(3, categoryList.size());
    }

    @Test
    public void TestGetAllCategories_ReturnsEmptyList(){
        // Arrange
        when(productRepository.findAllByState(any(State.class))).thenReturn(Collections.emptyList());

        // Act
        List<Product> categoryList = productService.getAllProducts();

        // Assert
        assertNotNull(categoryList);
        assertEquals(0, categoryList.size());
    }

    @Test
    public void TestCreateProduct_WIthValidProductDetails_RunsSuccessfully(){
        // Arrange
        long productId = 1L;
        Category category = Category.builder().id(1L).name("Test Category").build();
        Product product = Product.builder().name("Test Product").description("Test Description").category(category).build();
        Product productExpected = Product.builder().id(productId).name("Test Product").description("Test Description").category(category).build();
        when(productRepository.save(any(Product.class))).thenReturn(productExpected);
        // Act
        Product productResponse = productService.createProduct(product);
        // Assert
        assertNotNull(productResponse);
        assertEquals(productId, productResponse.getId());
        assertEquals("Test Product", productResponse.getName());
        assertEquals("Test Description", productResponse.getDescription());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void TestCreateProduct_WithNullProductDetails_ThrowsIllegalArgumentException() {
        // Act and Assert
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                ()->productService.createProduct(null));
        assertEquals("Product cannot be null", illegalArgumentException.getMessage());
    }

    @Test
    public void TestReplaceProduct_WithValidIdAndProductDetails_RunsSuccessfully(){
        // Arrange
        long productId = 1L;
        Category category = Category.builder().id(1L).name("Test Category").build();
        Product product = Product.builder().id(productId).name("Test Product update").description("Test Description update").category(category).build();
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product udpatedProduct = productService.replaceProductByID(productId, product);
        // Assert
        assertNotNull(product);
        assertEquals(productId, udpatedProduct.getId());
        assertEquals("Test Product update", udpatedProduct.getName());
        assertEquals("Test Description update", udpatedProduct.getDescription());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void TestReplaceProduct_WithInvalidId_ThrowsIllegalArgumentException() {
        // Arrange
        long productId = 1L;
        Category category = Category.builder().id(1L).name("Test Category").build();
        Product product = Product.builder().id(productId).name("Test Product update").description("Test Description update").category(category).build();
        when(productRepository.existsById(anyLong())).thenReturn(false);
        // Act
        Product udpatedProduct = productService.replaceProductByID(productId, product);
        // Assert
        assertNull(udpatedProduct);
    }

    @Test
    public void TestDeleteProduct_WithValidId_RunsSuccessfully(){
        // Arrange
        long productId = 1L;
        Product product = Product.builder().id(productId).name("Test Product update").description("Test Description update").build();
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        // Act
        boolean result = productService.deleteProductByID(productId);
        // Assert
        assertTrue(result);
    }
    @Test
    public void TestDeleteProduct_WithInValidId_RunsSuccessfully(){
        // Arrange
        long productId = 1L;
        Product product = Product.builder().id(productId).name("Test Product update").description("Test Description update").build();
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        // Act
        boolean result = productService.deleteProductByID(productId);
        // Assert
        assertFalse(result);
    }

}