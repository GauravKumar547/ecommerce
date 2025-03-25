package com.ecommerce.productcatalogservice.controllers;

import com.ecommerce.productcatalogservice.dtos.CategoryDTO;
import com.ecommerce.productcatalogservice.dtos.ResponseDTO;
import com.ecommerce.productcatalogservice.mappers.CategoryMapper;
import com.ecommerce.productcatalogservice.models.Category;
import com.ecommerce.productcatalogservice.services.impl.CategoryService;
import com.ecommerce.productcatalogservice.utils.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest( CategoryController.class)
@MockitoBean(types = JpaMetamodelMappingContext.class)
public class CategoryControllerTest {
    @MockitoBean
    @Qualifier("sqlCategoryService")
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void TestGetCategoryById_WithValidId_RunsSuccessfully() throws Exception {
        // Arrange
        long categoryId = 1L;
        Category category = Category.builder().id(categoryId).name("Test Category").description("Test Description").build();
        when(categoryService.getCategoryByID(anyLong())).thenReturn(category);
        ApiResponse<CategoryDTO> response = new ApiResponse<>();
        response.setData(CategoryMapper.toCategoryDTO(category)).setStatus(HttpStatus.OK);
        // Act and Assert
        mockMvc.perform(get("/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void TestGetCategoryById_WithInvalidId_ThrowsIllegalArgumentException() throws Exception {
        // Act and Assert
        mockMvc.perform(get("/categories/{id}", -1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Category id cannot be less than 1"));
    }

    @Test
    public void TestGetCategoryById_WithNonExistingId_ThrowsIllegalArgumentException() throws Exception {
        // Arrange
        long categoryId = 5L;
        when(categoryService.getCategoryByID(anyLong())).thenReturn(null);
        // Act and Assert
        mockMvc.perform(get("/categories/{id}", categoryId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Category not found"));
    }

    @Test
    public void TestGetAllCategories_RunsSuccessfully() throws Exception {
        // Arrange
        List<Category> categories = List.of(new Category(), new Category(), new Category());
        List<CategoryDTO> categoriesDTO = categories.stream().map(CategoryMapper::toCategoryDTO).toList();
        when(categoryService.getAllCategories()).thenReturn(categories);
        ApiResponse<List<CategoryDTO>> response = new ApiResponse<>();
        response.setData(categoriesDTO).setStatus(HttpStatus.OK);
        // Act and Assert
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void TestGetAllCategories_ReturnsEmptyList() throws Exception {
        // Arrange
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());
        ApiResponse<List<CategoryDTO>> response = new ApiResponse<>();
        response.setData(Collections.emptyList()).setStatus(HttpStatus.OK);

        // Act and Assert
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void TestCreateCategory_WIthValidCategoryDetails_RunsSuccessfully() throws Exception{
        // Arrange
        long categoryId = 1L;
        Category category = Category.builder().name("Test Category").description("Test Description").build();
        Category categoryExpected = Category.builder().id(categoryId).name("Test Category").description("Test Description").build();
        when(categoryService.createCategory(any(Category.class))).thenReturn(categoryExpected);
        ApiResponse<CategoryDTO> response = new ApiResponse<>();
        response.setData(CategoryMapper.toCategoryDTO(categoryExpected)).setStatus(HttpStatus.CREATED);
        // Act and Assert
        mockMvc.perform(post("/categories").content(objectMapper.writeValueAsString(CategoryMapper.toCategoryDTO(category))).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(content().string(objectMapper.writeValueAsString(response)));
        verify(categoryService, times(1)).createCategory(any(Category.class));
    }

    @Test
    public void TestCreateCategory_WithNullCategoryDetails_ThrowsIllegalArgumentException() throws Exception{
        // Act and Assert
        mockMvc.perform(post("/categories").content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void TestCreateCategory_WithNullCategoryName_ThrowsIllegalArgumentException() throws Exception {
        // Act and Assert
        mockMvc.perform(post("/categories").content(objectMapper.writeValueAsString(new CategoryDTO())).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Category name cannot be null or empty"));
    }

    @Test
    public void TestCreateCategory_WithEmptyCategoryName_ThrowsIllegalArgumentException() throws Exception {
        // Arrange
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("");
        // Act and Assert
        mockMvc.perform(post("/categories").content(objectMapper.writeValueAsString(categoryDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Category name cannot be null or empty"));
    }

    @Test
    public void TestReplaceCategory_WithValidIdAndCategoryDetails_RunsSuccessfully() throws Exception{
       // Arrange
        long categoryId = 1L;
        Category category = Category.builder().id(categoryId).name("Test Category update").description("Test Description update").build();
        when(categoryService.replaceCategoryByID(anyLong(), any(Category.class))).thenReturn(category);
        ApiResponse<CategoryDTO> response = new ApiResponse<>();
        response.setData(CategoryMapper.toCategoryDTO(category)).setStatus(HttpStatus.OK);
        // Act and Assert
        mockMvc.perform(put("/categories/{id}",categoryId).content(objectMapper.writeValueAsString(CategoryMapper.toCategoryDTO(category))).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
        verify(categoryService, times(1)).replaceCategoryByID(anyLong(), any(Category.class));
    }

    @Test
    public void TestReplaceCategory_WithInvalidId_ThrowsIllegalArgumentException() throws Exception {
        // Act and Assert
        mockMvc.perform(put("/categories/{id}",-1L).content(objectMapper.writeValueAsString(new CategoryDTO())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Category id cannot be less than 1"));
    }

    @Test
    public void TestReplaceCategory_WithNullCategory_ThrowsIllegalArgumentException() throws Exception {
        // Act and Assert
        mockMvc.perform(put("/categories/{id}",1L).content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }
    @Test
    public void TestDeleteCategory_WithValidId_RunsSuccessfully() throws Exception{
        // Arrange
        long categoryId = 1L;
        when(categoryService.deleteCategoryByID(anyLong())).thenReturn(true);
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Successfully deleted Category");
        ApiResponse<ResponseDTO> response = new ApiResponse<>();
        response.setData(responseDTO).setStatus(HttpStatus.OK);
        // Act and Assert
        mockMvc.perform(delete("/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }
    @Test
    public void TestDeleteCategory_WithInvalidId_ReturnsError() throws Exception {
        // Act and Assert
        mockMvc.perform(delete("/categories/{id}",-1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Category id cannot be less than 1"));

    }
    @Test
    public void TestDeleteCategory_WithIdHavingNoCategory_ReturnsNotFound() throws Exception {
        // Arrange
        long categoryId = 1L;
        when(categoryService.deleteCategoryByID(anyLong())).thenReturn(false);
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Failed to delete Category");
        ApiResponse<ResponseDTO> response = new ApiResponse<>();
        response.setData(responseDTO).setStatus(HttpStatus.NOT_FOUND);
        // Act and Assert
        mockMvc.perform(delete("/categories/{id}", categoryId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));

    }

    @Test
    public void TestUpdateCategoryFields_WithValidIdAndCategory_RunsSuccessfully() throws Exception{
        // Arrange
        long categoryId = 1L;
        Category category = Category.builder().id(categoryId).name("Test Category").description("Test Description").build();
        Category categoryExpected = Category.builder().id(categoryId).name("Test Category").description("Test Description update").build();
        when(categoryService.replaceCategoryByID(anyLong(), any(Category.class))).thenReturn(categoryExpected);
        when(categoryService.getCategoryByID(anyLong())).thenReturn(category);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setDescription("Test Description update");

        ApiResponse<CategoryDTO> response = new ApiResponse<>();
        response.setData(CategoryMapper.toCategoryDTO(categoryExpected)).setStatus(HttpStatus.OK);

        // Act and Assert
        mockMvc.perform(patch("/categories/{id}",categoryId).content(objectMapper.writeValueAsString(categoryDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
        verify(categoryService, times(1)).replaceCategoryByID(anyLong(), any(Category.class));
    }

    @Test
    public void TestUpdateCategoryFields_WithInvalidId_ReturnsError() throws Exception {
        // Act and Assert
        mockMvc.perform(patch("/categories/{id}",-1L).content(objectMapper.writeValueAsString(new CategoryDTO())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Category id cannot be less than 1"));
    }
    @Test
    public void TestUpdateCategoryFields_WithValidIdAndNullCategory_ReturnsError() throws Exception {
        // Act and Assert
        mockMvc.perform(patch("/categories/{id}",1L).content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }
    @Test
    public void TestUpdateCategoryFields_WithIdWhoseCategoryIsNotAvailable_ReturnsNotFoundMessage() throws Exception {
        // Arrange
        ApiResponse<ResponseDTO> response = new ApiResponse<>();
        response.setError("Category not found to update").setStatus(HttpStatus.NOT_FOUND);
        // Act and Assert
        mockMvc.perform(patch("/categories/{id}",1L).content(objectMapper.writeValueAsString(new CategoryDTO())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void TestUpdateCategoryFields_WithValidIdAndCategoryWithNoFields_RunsSuccessfully() throws Exception{
        // Arrange
        long categoryId = 1L;
        Category category = Category.builder().id(categoryId).name("Test Category").description("Test Description").build();
        when(categoryService.replaceCategoryByID(anyLong(), any(Category.class))).thenReturn(category);
        when(categoryService.getCategoryByID(anyLong())).thenReturn(category);

        ApiResponse<CategoryDTO> response = new ApiResponse<>();
        response.setData(CategoryMapper.toCategoryDTO(category)).setStatus(HttpStatus.OK);

        // Act and Assert
        mockMvc.perform(patch("/categories/{id}",categoryId).content(objectMapper.writeValueAsString(new CategoryDTO())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
        verify(categoryService, times(1)).replaceCategoryByID(anyLong(), any(Category.class));
    }
}