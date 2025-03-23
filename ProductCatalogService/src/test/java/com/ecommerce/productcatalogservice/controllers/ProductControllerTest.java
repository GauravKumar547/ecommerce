package com.ecommerce.productcatalogservice.controllers;

import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import com.ecommerce.productcatalogservice.dtos.ResponseDTO;
import com.ecommerce.productcatalogservice.mappers.CategoryMapper;
import com.ecommerce.productcatalogservice.mappers.ProductMapper;
import com.ecommerce.productcatalogservice.models.Category;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.services.IProductService;
import com.ecommerce.productcatalogservice.utils.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    public void TestGetProduct_WithValidId_RunsSuccessfully() throws Exception {
        // Arrange
        long id = 3L;
        Product product = new Product();
        product.setId(id);
        when(productService.getProductByID(anyLong())).thenReturn(product);
        ApiResponse<ProductDTO> response = new ApiResponse<>();
        response.setData(ProductMapper.toProductDTO(product)).setStatus(HttpStatus.OK);
        // Act and Assert
        mockMvc.perform(get("/products/{id}",id))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void TestGetProduct_WithNegativeId_ThrowsIllegalArgumentException() throws Exception {
        // Act and Assert
        mockMvc.perform(get("/products/{id}", -1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Product id must be greater than 0"));
    }

    @Test
    public void TestGetProduct_WithUnavailableId_ReturnsNotFound() throws Exception {
        // Arrange
        long id = 4L;

        // Act and Assert
        mockMvc.perform(get("/products/{id}", id)).andExpect(status().isNotFound()).andExpect(jsonPath("$.error").value("Product not found"));
    }
    @Test
    public void TestGetProduct_ProductServiceCalledWithCorrectArguments_RunsSuccessfully() throws Exception {
        // Arrange
        long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("TestProd");
        when(productService.getProductByID(productId)).thenReturn(product);
        ApiResponse<ProductDTO> response = new ApiResponse<>();
        response.setData(ProductMapper.toProductDTO(product)).setStatus(HttpStatus.OK);
        // Act and Assert
        mockMvc.perform(get("/products/{id}", productId)).
                andExpect(status().isOk()).andExpect(content().string(objectMapper.writeValueAsString(response)));

        verify(productService).getProductByID(idCaptor.capture());
        assertEquals(productId, idCaptor.getValue());
    }

    @Test
    public void TestAddProduct_withCompleteValidProduct_RunsSuccessfully() throws Exception {
        // Arrange
        long productId = 1L;
        Category category = Category.builder().name("test").id(1L).build();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(productId);
        productDTO.setName("TestProd");
        productDTO.setDescription("testing desc for product");
        productDTO.setCategory(CategoryMapper.toCategoryDTO(category));

        Product productExpected = Product.builder().id(productId).name("TestProd").category(category).description("testing desc for product").build();
        when(productService.createProduct(any(Product.class))).thenReturn(productExpected);
        ApiResponse<ProductDTO> response = new ApiResponse<>();
        response.setData(ProductMapper.toProductDTO(productExpected)).setStatus(HttpStatus.CREATED);

        // Act and Assert
        mockMvc.perform(post("/products").content(objectMapper.writeValueAsString(productDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    public void TestAddProduct_WithEmptyProduct_ReturnsErrorMessage() throws Exception {
        // Act and Assert
        mockMvc.perform(post("/products").content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void TestAddProduct_WithNullCategory_ThrowsIllegalArgumentException() throws Exception {
        // Arrange
        ProductDTO productDTO = new ProductDTO();
        // Act and Assert
        mockMvc.perform(post("/products").content(objectMapper.writeValueAsString(productDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Category cannot be null"));
    }

    @Test
    public void TestDeleteProduct_WithValidId_RunsSuccessfully() throws Exception {
        // Arrange
        long productId = 1L;
        when(productService.deleteProductByID(anyLong())).thenReturn(true);

        // Act and Assert
        mockMvc.perform(delete("/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Delete product successful"));
        verify(productService, times(1)).deleteProductByID(anyLong());
    }

    @Test
    public void TestDeleteProduct_WithNegativeId_ThrowsIllegalArgumentException() throws Exception {
        // Act and Assert
        mockMvc.perform(delete("/products/{id}",-1L))
                .andExpect(jsonPath("$.error").value("Product id must be greater than 0"));
    }

    @Test
    public void TestDeleteProduct_WithInValidId_FailsSuccessfully() throws Exception {
        // Arrange
        long productId = 5L;
        when(productService.deleteProductByID(anyLong())).thenReturn(false);

        // Act and Assert
        mockMvc.perform(delete("/products/{id}", productId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.message").value("Delete product failed"));
        verify(productService, times(1)).deleteProductByID(anyLong());
    }

    @Test
    public void TestReplaceProduct_WithValidId_RunsSuccessfully() throws Exception {
        // Arrange
        long productId = 1L;
        Category category = Category.builder().name("test").id(1L).build();
        Product product = Product.builder().id(productId).name("TestProd").category(category).description("testing " +
                "desc updated for product").build();
        Product productExpected = Product.builder().id(productId).name("TestProd123").category(category).description(
                "testing desc for product").build();
        when(productService.replaceProductByID(anyLong(), any(Product.class))).thenReturn(productExpected);
        ApiResponse<ProductDTO> response = new ApiResponse<>();
        response.setData(ProductMapper.toProductDTO(productExpected)).setStatus(HttpStatus.OK);

        // Act and Assert
        mockMvc.perform(put("/products/{id}", productId).content(objectMapper.writeValueAsString(ProductMapper.toProductDTO(product))).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));

        verify(productService, times(1)).replaceProductByID(anyLong(),any(Product.class));
    }

    @Test
    public void TestReplaceProduct_WithInvalidId_ThrowsIllegalArgumentException() throws Exception {
        // Act and Assert
        mockMvc.perform(put("/products/{id}",-1L)
                .content(objectMapper.writeValueAsString(new ProductDTO()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Product id must be greater than 0"));
    }

    @Test
    public void TestReplaceProduct_WithNullProduct_ThrowsIllegalArgumentException() throws Exception {
        // Act and Assert
        mockMvc.perform(put("/products/{id}", 1L).content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void TestReplaceProduct_WithServiceThrowingException_ThrowsException() throws Exception {
        // Arrange
        when(productService.replaceProductByID(anyLong(),any(Product.class))).thenThrow(new RuntimeException("Service error"));
        // Act and Assert
        mockMvc.perform(put("/products/{id}", 1L).content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void TestGetAllProducts_ReturnProductList() throws Exception {
        // Arrange
        List<Product> productList = List.of(new Product(), new Product(), new Product());
        when(productService.getAllProducts()).thenReturn(productList);
        ApiResponse<List<ProductDTO>> response = new ApiResponse<>();
        response.setData(productList.stream().map(ProductMapper::toProductDTO).toList()).setStatus(HttpStatus.OK);
        // Act and Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void TestGetAllProducts_ReturnNull() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());
        ApiResponse<List<ProductDTO>> response = new ApiResponse<>();
        response.setData(Collections.emptyList()).setStatus(HttpStatus.OK);
        // Act and Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().string( objectMapper.writeValueAsString(response)));
    }

    @Test
    public void TestGetProductByCategory_WithValidCategoryName_RunsSuccessfully() throws Exception {
        // Arrange
        String categoryName = "test";
        Category category = Category.builder().name(categoryName).id(1L).build();
        List<Product> products = List.of(Product.builder().category(category).build(), new Product(), new Product());
        when(productService.getAllProducts()).thenReturn(products);
        ApiResponse<List<ProductDTO>> response = new ApiResponse<>();
        response.setData(products.stream().filter(product->product.getCategory()!=null&&categoryName.equals(product.getCategory().getName())).map(ProductMapper::toProductDTO).toList()).setStatus(HttpStatus.OK);
        // Act and Assert
        mockMvc.perform(get("/products/category/{categoryName}", categoryName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
        verify(productService, times(1)).getAllProducts();
    }


    @Test
    public void TestGetProductByCategory_WithNoProductForCategory_ReturnsEmptyList() throws Exception {
        // Arrange
        String categoryName = "test";
        Category category = Category.builder().name("test23").id(1L).build();
        List<Product> products = List.of(Product.builder().category(category).build(),
                Product.builder().category(category).build(), new Product());
        when(productService.getAllProducts()).thenReturn(products);
        ApiResponse<List<ProductDTO>> response = new ApiResponse<>();
        response.setData(Collections.emptyList()).setStatus(HttpStatus.OK);
        // Act and Assert
        mockMvc.perform(get("/products/category/{categoryName}", categoryName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    public void TestGetProductByCategory_WithInvalidCategoryName_ThrowsIllegalArgumentException() throws Exception {
        // Act and Assert
        mockMvc.perform(get("/products/category/ "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Category name cannot be empty"));
    }

    @Test
    public void TestUpdateProduct_WithValidIdAndProduct_RunsSuccessfully() throws Exception {
        long productId = 1L;
        Category category = Category.builder().name("test").id(1L).build();
        Product product = Product.builder().id(productId).name("TestProd").category(category).description("testing " +
                "desc updated for product").build();
        Product productExpected = Product.builder().id(productId).name("TestProd123").category(category).description(
                "testing desc for product").build();
        when(productService.getProductByID(anyLong())).thenReturn(product);
        when(productService.replaceProductByID(anyLong(), any(Product.class))).thenReturn(productExpected);
        ApiResponse<ProductDTO> response = new ApiResponse<>();
        response.setData(ProductMapper.toProductDTO(productExpected)).setStatus(HttpStatus.OK);
        // Act and Assert
        mockMvc.perform(patch("/products/{id}", productId).content(objectMapper.writeValueAsString(ProductMapper.toProductDTO(product))).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
        verify(productService, times(1)).replaceProductByID(anyLong(),any(Product.class));
        verify(productService, times(1)).getProductByID(anyLong());
    }
    @Test
    public void TestUpdateProduct_WithValidIdAndProductWithOneField_RunsSuccessfully() throws Exception {
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

        ApiResponse<ProductDTO> response = new ApiResponse<>();
        response.setData(ProductMapper.toProductDTO(productExpected)).setStatus(HttpStatus.OK);
        // Act and Assert
        mockMvc.perform(patch("/products/{id}", productId).content(objectMapper.writeValueAsString(ProductMapper.toProductDTO(product))).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
        verify(productService, times(1)).replaceProductByID(anyLong(),any(Product.class));
        verify(productService, times(1)).getProductByID(anyLong());
    }

    @Test
    public void TestUpdateProduct_WIthInvalidId_ThrowsIllegalArgumentException() throws Exception {
        // Act and Assert
        mockMvc.perform(patch("/products/{id}", -1L).content(objectMapper.writeValueAsString(new ProductDTO())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Product id must be greater than 0"));
    }

    @Test
    public void TestUpdateProduct_WithNullProduct_ThrowsIllegalArgumentException() throws Exception {
        // Act and Assert
        mockMvc.perform(patch("/products/{id}", 1L).content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }
    @Test
    public void TestUpdateProduct_WithNonExistingProductId_ThrowsIllegalArgumentException() throws Exception {
        // Arrange
        long id = 5L;
        when(productService.getProductByID(anyLong())).thenReturn(null);

        // Act and Assert
        mockMvc.perform(patch("/products/{id}", id).content(objectMapper.writeValueAsString(new ProductDTO())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Product with given id not found"));

    }

    @Test
    public void TestUpdateProduct_WithValidIdAndProductWithEmptyFields_RunsSuccessfully() throws Exception {
        // Arrange
        long productId = 1L;
        Category category = Category.builder().name("test").id(1L).build();
        Product product = Product.builder().id(productId).name("TestProd").category(category).description(
                "testing desc for product").build();
        when(productService.getProductByID(anyLong())).thenReturn(product);
        when(productService.replaceProductByID(anyLong(), any(Product.class))).thenReturn(product);
        ApiResponse<ProductDTO> response = new ApiResponse<>();
        response.setData(ProductMapper.toProductDTO(product)).setStatus(HttpStatus.OK);

        // Act and Assert
        mockMvc.perform(patch("/products/{id}", productId).content(objectMapper.writeValueAsString(ProductMapper.toProductDTO(new Product()))).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(productService, times(1)).replaceProductByID(anyLong(),any(Product.class));
        verify(productService, times(1)).getProductByID(anyLong());

    }


}