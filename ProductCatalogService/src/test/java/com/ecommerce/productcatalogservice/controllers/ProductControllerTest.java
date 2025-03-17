package com.ecommerce.productcatalogservice.controllers;

import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import com.ecommerce.productcatalogservice.dtos.ResponseDTO;
import com.ecommerce.productcatalogservice.mappers.CategoryMapper;
import com.ecommerce.productcatalogservice.mappers.ProductMapper;
import com.ecommerce.productcatalogservice.models.Category;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.services.IProductService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProductControllerTest {
    @MockitoBean
    @Qualifier("sqlProductService")
    private IProductService productService;

    @Autowired
    private ProductController productController;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

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

    @Test
    public void TestGetProduct_WithUnavailableId_ReturnsNull(){
        // Arrange
        long id = 4L;

        // Act
        ProductDTO productDTO = productController.getProduct(id);

        // Assert
        assertNull(productDTO);
    }
    @Test
    public void TestGetProduct_ProductServiceCalledWithCorrectArguments_RunsSuccessfully() {
        // Arrange
        long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("TestProd");
        when(productService.getProductByID(productId)).thenReturn(product);

        // Act
        productController.getProduct(productId);

        // Assert
        verify(productService).getProductByID(idCaptor.capture());
        assertEquals(productId, idCaptor.getValue());
    }

    @Test
    public void TestAddProduct_withCompleteValidProduct_RunsSuccessfully(){
        // Arrange
        Long productId = 1L;
        Category category = Category.builder().name("test").id(1L).build();
        Product product = Product.builder().name("TestProd").category(category).description("testing desc for product").build();
        Product productExpected = Product.builder().id(productId).name("TestProd").category(category).description("testing desc for product").build();
        when(productService.createProduct(any(Product.class))).thenReturn(productExpected);

        // Act
        ProductDTO productDTO =  productController.addProduct(ProductMapper.toProductDTO(product));

        // Assert
        assertNotNull(productDTO);
        assertEquals(productId, productDTO.getId());
        assertEquals(product.getName(), productDTO.getName());
        assertEquals(product.getDescription(), productDTO.getDescription());
        assertNotNull(productDTO.getCategory());
        assertEquals(product.getCategory().getId(), productDTO.getCategory().getId());
        assertEquals(product.getCategory().getName(), productDTO.getCategory().getName());
        assertEquals(product.getCategory().getDescription(), productDTO.getCategory().getDescription());
        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    public void TestAddProduct_WithNullProduct_ThrowsIllegalArgumentException() {
        // Act and Assert
        IllegalArgumentException Exception =  assertThrows(IllegalArgumentException.class,()->productController.addProduct(null));
        assertEquals("Product cannot be null", Exception.getMessage());
    }

    @Test
    public void TestAddProduct_WithNullCategory_ThrowsIllegalArgumentException() {
        // Act and Assert
        IllegalArgumentException Exception =  assertThrows(IllegalArgumentException.class,()->productController.addProduct(new ProductDTO()));
        assertEquals("Category cannot be null", Exception.getMessage());
    }

    @Test
    public void TestDeleteProduct_WithValidId_RunsSuccessfully() {
        // Arrange
        long productId = 1L;
        when(productService.deleteProductByID(anyLong())).thenReturn(true);

        // Act
        ResponseDTO responseDTO =  productController.deleteProduct(productId);

        // Assert
        assertEquals("Delete product successful", responseDTO.getMessage());
        verify(productService, times(1)).deleteProductByID(anyLong());
    }

    @Test
    public void TestDeleteProduct_WithNegativeId_ThrowsIllegalArgumentException() {
        // Act and Assert
        IllegalArgumentException Exception =  assertThrows(IllegalArgumentException.class,()->productController.deleteProduct(-1L));
        assertEquals("Product id must be greater than 0", Exception.getMessage());
    }

    @Test
    public void TestDeleteProduct_WithInValidId_FailsSuccessfully() {
        // Arrange
        long productId = 5L;
        when(productService.deleteProductByID(anyLong())).thenReturn(false);

        // Act
        ResponseDTO responseDTO =  productController.deleteProduct(productId);

        // Assert
        assertEquals("Delete product failed", responseDTO.getMessage());
        verify(productService, times(1)).deleteProductByID(anyLong());
    }

    @Test
    public void TestReplaceProduct_WithValidId_RunsSuccessfully() {

    }

}