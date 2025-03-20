package com.ecommerce.productcatalogservice.controllers;

import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import com.ecommerce.productcatalogservice.dtos.ResponseDTO;
import com.ecommerce.productcatalogservice.mappers.CategoryMapper;
import com.ecommerce.productcatalogservice.mappers.ProductMapper;
import com.ecommerce.productcatalogservice.models.Category;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.services.IProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@MockitoBean(types = JpaMetamodelMappingContext.class)
public class ProductControllerTest {
    @MockitoBean
    @Qualifier("sqlProductService")
    private IProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    @Autowired
    private ProductController productController;

    @Test
    public void TestGetProduct_WithValidId_RunsSuccessfully() throws Exception {
        // Arrange
        long id = 3L;
        Product product = new Product();
        product.setId(id);
        when(productService.getProductByID(anyLong())).thenReturn(product);
        // Act and Assert
        mockMvc.perform(get("/products/{id}",id))
                .andExpect(content().string(objectMapper.writeValueAsString(ProductMapper.toProductDTO(product))));
    }

    @Test
    public void TestGetProduct_WithNegativeId_ThrowsIllegalArgumentException() throws Exception {
        // Act and Assert
        mockMvc.perform(get("/products/{id}", -1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product id must be greater than 0"));
    }

    @Test
    public void TestGetProduct_WithUnavailableId_ReturnsNull() throws Exception {
        // Arrange
        long id = 4L;

        // Act and Assert
        mockMvc.perform(get("/products/{id}", id)).andExpect(status().isOk()).andExpect(content().string(""));
    }
    @Test
    public void TestGetProduct_ProductServiceCalledWithCorrectArguments_RunsSuccessfully() throws Exception {
        // Arrange
        long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("TestProd");
        when(productService.getProductByID(productId)).thenReturn(product);

        // Act and Assert
        mockMvc.perform(get("/products/{id}", productId)).
                andExpect(status().isOk()).andExpect(content().string(objectMapper.writeValueAsString(ProductMapper.toProductDTO(product))));

        verify(productService).getProductByID(idCaptor.capture());
        assertEquals(productId, idCaptor.getValue());
    }

    @Test
    public void TestAddProduct_withCompleteValidProduct_RunsSuccessfully() throws Exception {
        // Arrange
        Long productId = 1L;
        Category category = Category.builder().name("test").id(1L).build();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(productId);
        productDTO.setName("TestProd");
        productDTO.setDescription("testing desc for product");
        productDTO.setCategory(CategoryMapper.toCategoryDTO(category));

        Product productExpected = Product.builder().id(productId).name("TestProd").category(category).description("testing desc for product").build();
        when(productService.createProduct(any(Product.class))).thenReturn(productExpected);

        // Act and Assert
        mockMvc.perform(post("/products").content(objectMapper.writeValueAsString(productDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(ProductMapper.toProductDTO(productExpected))));
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
        // Arrange
        long productId = 1L;
        Category category = Category.builder().name("test").id(1L).build();
        Product product = Product.builder().id(productId).name("TestProd").category(category).description("testing " +
                "desc updated for product").build();
        Product productExpected = Product.builder().id(productId).name("TestProd123").category(category).description(
                "testing desc for product").build();
        when(productService.replaceProductByID(anyLong(), any(Product.class))).thenReturn(productExpected);

        // Act
        ProductDTO productDTO = productController.replaceProduct(productId,ProductMapper.toProductDTO(productExpected));

        // Assert
        assertNotNull(productDTO);
        assertNotEquals(product.getName(), productDTO.getName());
        assertNotEquals(product.getDescription(), productDTO.getDescription());
        assertEquals(productExpected.getName(), productDTO.getName());
        assertEquals(productExpected.getDescription(), productDTO.getDescription());
        verify(productService, times(1)).replaceProductByID(anyLong(),any(Product.class));
    }

    @Test
    public void TestReplaceProduct_WithInvalidId_ThrowsIllegalArgumentException() {
        // Act and Assert
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                ()-> productController.replaceProduct(-1L,new ProductDTO()));
        assertEquals("Product id must be greater than 0", illegalArgumentException.getMessage());
    }
    @Test
    public void TestReplaceProduct_WithNullProduct_ThrowsIllegalArgumentException() {
        // Act and Assert
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                ()-> productController.replaceProduct(1L,null));
        assertEquals("Product cannot be null", illegalArgumentException.getMessage());
    }

    @Test
    public void TestReplaceProduct_WithServiceThrowingException_ThrowsIllegalArgumentException() {
        // Arrange
        when(productService.replaceProductByID(anyLong(),any(Product.class))).thenThrow(new RuntimeException("Service" +
                " error"));
        // Act and Assert
        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                ()-> productController.replaceProduct(1L,new ProductDTO()));
        assertEquals("Service error", runtimeException.getMessage());
    }

    @Test
    public void TestGetAllProducts_ReturnProductList() {
        // Arrange
        when(productService.getAllProducts()).thenReturn(List.of(new Product(), new Product(), new Product()));

        // Act
        List<ProductDTO> productDTOList = productController.getAllProducts();

        // Assert
        assertNotNull(productDTOList);
        assertEquals(3, productDTOList.size());
    }

    @Test
    public void TestGetAllProducts_ReturnNull() {
        // Arrange
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        // Act
        List<ProductDTO> productDTOList = productController.getAllProducts();

        // Assert
        assertNull(productDTOList);
    }

    @Test
    public void TestGetProductByCategory_WithValidCategoryName_RunsSuccessfully() {
        // Arrange
        String categoryName = "test";
        Category category = Category.builder().name(categoryName).id(1L).build();
        List<Product> products = List.of(Product.builder().category(category).build(), new Product(), new Product());
        when(productService.getAllProducts()).thenReturn(products);

        // Act
        List<ProductDTO> productDTOList = productController.getProductsByCategory(categoryName);

        // Assert
        assertNotNull(productDTOList);
        assertEquals(1, productDTOList.size());
        verify(productService, times(1)).getAllProducts();
    }


    @Test
    public void TestGetProductByCategory_WithNoProductForCategory_ReturnsEmptyList() {
        // Arrange
        String categoryName = "test";
        Category category = Category.builder().name("test23").id(1L).build();
        List<Product> products = List.of(Product.builder().category(category).build(),
                Product.builder().category(category).build(), new Product());
        when(productService.getAllProducts()).thenReturn(products);

        // Act
        List<ProductDTO> productDTOList = productController.getProductsByCategory(categoryName);

        // Assert
        assertNotNull(productDTOList);
        assertEquals(0, productDTOList.size());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    public void TestGetProductByCategory_WithNullCategoryName_ThrowsIllegalArgumentException() {
        // Act and Assert
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                ()->productController.getProductsByCategory(null));
        assertEquals("Category name cannot be null", illegalArgumentException.getMessage());
    }

    @Test
    public void TestGetProductByCategory_WithInvalidCategoryName_ThrowsIllegalArgumentException() {
        // Act and Assert
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                ()->productController.getProductsByCategory(""));
        assertEquals("Category name cannot be empty", illegalArgumentException.getMessage());
    }

    @Test
    public void TestUpdateProduct_WithValidIdAndProduct_RunsSuccessfully() {
        long productId = 1L;
        Category category = Category.builder().name("test").id(1L).build();
        Product product = Product.builder().id(productId).name("TestProd").category(category).description("testing " +
                "desc updated for product").build();
        Product productExpected = Product.builder().id(productId).name("TestProd123").category(category).description(
                "testing desc for product").build();
        when(productService.replaceProductByID(anyLong(), any(Product.class))).thenReturn(productExpected);

        // Act
        ProductDTO productDTO = productController.replaceProduct(productId,ProductMapper.toProductDTO(productExpected));

        // Assert
        assertNotNull(productDTO);
        assertNotEquals(product.getName(), productDTO.getName());
        assertNotEquals(product.getDescription(), productDTO.getDescription());
        assertEquals(productExpected.getName(), productDTO.getName());
        assertEquals(productExpected.getDescription(), productDTO.getDescription());
        verify(productService, times(1)).replaceProductByID(anyLong(),any(Product.class));
    }
    @Test
    public void TestUpdateProduct_WithValidIdAndProductWithOneField_RunsSuccessfully() {
        long productId = 1L;
        Category category = Category.builder().name("test").id(1L).build();
        Product product = Product.builder().id(productId).name("TestProd").category(category).description(
                "testing desc for product").build();
        Product productExpected = Product.builder().id(productId).name("TestProd123").category(category).description(
                "testing desc for product").build();
        ProductDTO updateFieldProduct = new ProductDTO();
        updateFieldProduct.setName("TestProd123");
        when(productService.replaceProductByID(anyLong(), any(Product.class))).thenReturn(productExpected);
        when(productService.getProductByID(anyLong())).thenReturn(product);

        // Act
        ProductDTO productDTO = productController.updateProduct(productId,updateFieldProduct);

        // Assert
        assertNotNull(productDTO);
        System.out.println(productDTO.getName());
        System.out.println(product.getName());
        System.out.println(productExpected.getName());
        assertNotEquals("test", productDTO.getName());
        assertEquals(productExpected.getName(), productDTO.getName());
        assertEquals(product.getDescription(), productDTO.getDescription());
        verify(productService, times(1)).replaceProductByID(anyLong(),any(Product.class));
        verify(productService, times(1)).getProductByID(anyLong());
    }

    @Test
    public void TestUpdateProduct_WIthInvalidId_ThrowsIllegalArgumentException() {
        // Act and Assert
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, ()->productController.updateProduct(-1L, new ProductDTO()));
        assertEquals("Product id must be greater than 0", illegalArgumentException.getMessage());
    }

    @Test
    public void TestUpdateProduct_WithNullProduct_ThrowsIllegalArgumentException() {
        // Act and Assert
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, ()->productController.updateProduct(1L, null));
        assertEquals("Updating data of product cannot be null", illegalArgumentException.getMessage());
    }
    @Test
    public void TestUpdateProduct_WithNonExistingProductId_ThrowsIllegalArgumentException() {
        // Arrange
        long id = 5L;
        when(productService.getProductByID(anyLong())).thenReturn(null);

        // Act and Assert
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,()-> productController.updateProduct(id, new ProductDTO()));
        assertEquals("Product with given id not found", illegalArgumentException.getMessage());

    }

    @Test
    public void TestUpdateProduct_WithValidIdAndProductWithEmptyFields_RunsSuccessfully(){
        // Arrange
        long productId = 1L;
        Category category = Category.builder().name("test").id(1L).build();
        Product product = Product.builder().id(productId).name("TestProd").category(category).description(
                "testing desc for product").build();
        when(productService.getProductByID(anyLong())).thenReturn(product);
        when(productService.replaceProductByID(anyLong(), any(Product.class))).thenReturn(product);

        // Act
        ProductDTO productDTO= productController.updateProduct(productId,new ProductDTO());

        // Assert
        assertNotNull(productDTO);
        assertEquals("TestProd", productDTO.getName());
        assertEquals("testing desc for product", productDTO.getDescription());
        verify(productService, times(1)).replaceProductByID(anyLong(),any(Product.class));
        verify(productService, times(1)).getProductByID(anyLong());

    }


}