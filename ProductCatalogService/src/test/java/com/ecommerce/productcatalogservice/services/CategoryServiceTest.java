package com.ecommerce.productcatalogservice.services;

import com.ecommerce.productcatalogservice.controllers.CategoryController;
import com.ecommerce.productcatalogservice.dtos.CategoryDTO;
import com.ecommerce.productcatalogservice.mappers.CategoryMapper;
import com.ecommerce.productcatalogservice.models.Category;
import com.ecommerce.productcatalogservice.models.State;
import com.ecommerce.productcatalogservice.repos.CategoryRepository;
import com.ecommerce.productcatalogservice.services.impl.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CategoryServiceTest {
    @MockitoBean
    private CategoryRepository categoryRepository;

    @Autowired
    @Qualifier("sqlCategoryService")
    private CategoryService categoryService;

    @Test
    public void TestGetCategoryById_WithValidId_RunsSuccessfully() {
        // Arrange
        long categoryId = 1L;
        Category category = Category.builder().id(categoryId).name("Test Category").description("Test Description").build();
        Optional<Category> categoryOptional = Optional.of(category);
        when(categoryRepository.findById(anyLong())).thenReturn(categoryOptional);
        // Act
        Category fetchedCategory= categoryService.getCategoryByID(categoryId);
        // Assert
        assertNotNull(fetchedCategory);
        assertEquals(categoryId, fetchedCategory.getId());
        assertEquals("Test Category", fetchedCategory.getName());
        assertEquals("Test Description", fetchedCategory.getDescription());
    }

    @Test
    public void TestGetCategoryById_WithInvalidId_ReturnsNull() {
        // Act and Assert
        long categoryId = 5L;
        Optional<Category> categoryOptional = Optional.empty();
        when(categoryRepository.findById(anyLong())).thenReturn(categoryOptional);
        // Act
        Category fetchedCategory= categoryService.getCategoryByID(categoryId);
        assertNull(fetchedCategory);
    }

    @Test
    public void TestGetAllCategories_RunsSuccessfully(){
        // Arrange
        when(categoryRepository.findAllByState(any(State.class))).thenReturn(List.of(new Category(), new Category(),
                new Category()));

        // Act
        List<Category> categoryList = categoryService.getAllCategories();

        // Assert
        assertNotNull(categoryList);
        assertEquals(3, categoryList.size());
    }

    @Test
    public void TestGetAllCategories_ReturnsEmptyList(){
        // Arrange
        when(categoryRepository.findAllByState(any(State.class))).thenReturn(Collections.emptyList());

        // Act
        List<Category> categoryList = categoryService.getAllCategories();

        // Assert
        assertNotNull(categoryList);
        assertEquals(0, categoryList.size());
    }

    @Test
    public void TestCreateCategory_WIthValidCategoryDetails_RunsSuccessfully(){
        // Arrange
        long categoryId = 1L;
        Category category = Category.builder().name("Test Category").description("Test Description").build();
        Category categoryExpected = Category.builder().id(categoryId).name("Test Category").description("Test Description").build();
        when(categoryRepository.save(any(Category.class))).thenReturn(categoryExpected);
        // Act
        Category categoryResponse = categoryService.createCategory(category);
        // Assert
        assertNotNull(categoryResponse);
        assertEquals(categoryId, categoryResponse.getId());
        assertEquals("Test Category", categoryResponse.getName());
        assertEquals("Test Description", categoryResponse.getDescription());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    public void TestCreateCategory_WithNullCategoryDetails_ThrowsIllegalArgumentException() {
        // Act and Assert
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                ()->categoryService.createCategory(null));
        assertEquals("Category cannot be null", illegalArgumentException.getMessage());
    }

    @Test
    public void TestReplaceCategory_WithValidIdAndCategoryDetails_RunsSuccessfully(){
      // Arrange
        long categoryId = 1L;
        Category category = Category.builder().id(categoryId).name("Test Category update").description("Test Description update").build();
        when(categoryRepository.existsById(anyLong())).thenReturn(true);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        Category udpatedCategory = categoryService.replaceCategoryByID(categoryId, category);
        // Assert
        assertNotNull(category);
        assertEquals(categoryId, udpatedCategory.getId());
        assertEquals("Test Category update", udpatedCategory.getName());
        assertEquals("Test Description update", udpatedCategory.getDescription());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    public void TestReplaceCategory_WithInvalidId_ThrowsIllegalArgumentException() {
        // Arrange
        long categoryId = 1L;
        Category category = Category.builder().id(categoryId).name("Test Category update").description("Test Description update").build();
        when(categoryRepository.existsById(anyLong())).thenReturn(false);
        // Act
        Category udpatedCategory = categoryService.replaceCategoryByID(categoryId, category);
        // Assert
        assertNull(udpatedCategory);
    }
    @Test
    public void TestDeleteCategory_WithValidId_RunsSuccessfully(){
        // Arrange
        long categoryId = 1L;
        Category category = Category.builder().id(categoryId).name("Test Category update").description("Test Description update").build();
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        // Act
        boolean result = categoryService.deleteCategoryByID(categoryId);
        // Assert
        assertTrue(result);
    }
    @Test
    public void TestDeleteCategory_WithInValidId_RunsSuccessfully(){
        // Arrange
        long categoryId = 1L;
        Category category = Category.builder().id(categoryId).name("Test Category update").description("Test Description update").build();
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        // Act
        boolean result = categoryService.deleteCategoryByID(categoryId);
        // Assert
        assertFalse(result);
    }

}