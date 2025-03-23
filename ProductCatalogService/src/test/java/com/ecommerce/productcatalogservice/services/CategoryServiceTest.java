//package com.ecommerce.productcatalogservice.services;
//
//import com.ecommerce.productcatalogservice.controllers.CategoryController;
//import com.ecommerce.productcatalogservice.dtos.CategoryDTO;
//import com.ecommerce.productcatalogservice.mappers.CategoryMapper;
//import com.ecommerce.productcatalogservice.models.Category;
//import com.ecommerce.productcatalogservice.services.impl.CategoryService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//public class CategoryServiceTest {
//    @MockitoBean
//    private CategoryService categoryService;
//
//    @Autowired
//    private CategoryController categoryController;
//
//    @Test
//    public void TestGetCategoryById_WithValidId_RunsSuccessfully() {
//        // Arrange
//        long categoryId = 1L;
//        Category category = Category.builder().id(categoryId).name("Test Category").description("Test Description").build();
//        when(categoryService.getCategoryByID(anyLong())).thenReturn(category);
//        // Act
//        CategoryDTO categoryDTO = categoryController.getCategoryById(categoryId);
//        // Assert
//        assertNotNull(categoryDTO);
//        assertEquals(categoryId, categoryDTO.getId());
//        assertEquals("Test Category", categoryDTO.getName());
//        assertEquals("Test Description", categoryDTO.getDescription());
//    }
//
//    @Test
//    public void TestGetCategoryById_WithInvalidId_ThrowsIllegalArgumentException() {
//        // Act and Assert
//        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,()->categoryController.getCategoryById(-1L));
//        assertEquals("Category id cannot be less than 1", illegalArgumentException.getMessage());
//    }
//
//    @Test
//    public void TestGetCategoryById_WithNonExistingId_ThrowsIllegalArgumentException() {
//        // Arrange
//        long categoryId = 5L;
//        when(categoryService.getCategoryByID(anyLong())).thenReturn(null);
//        // Act
//        CategoryDTO categoryDTO = categoryController.getCategoryById(categoryId);
//        // Assert
//        assertNull(categoryDTO);
//    }
//
//    @Test
//    public void TestGetAllCategories_RunsSuccessfully(){
//        // Arrange
//        when(categoryService.getAllCategories()).thenReturn(List.of(new Category(), new Category(), new Category()));
//
//        // Act
//        List<CategoryDTO> categoryDTOList = categoryController.getAllCategories();
//
//        // Assert
//        assertNotNull(categoryDTOList);
//        assertEquals(3, categoryDTOList.size());
//    }
//
//    @Test
//    public void TestGetAllCategories_ReturnsEmptyList(){
//        // Arrange
//        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());
//
//        // Act
//        List<CategoryDTO> categoryDTOList = categoryController.getAllCategories();
//
//        // Assert
//        assertNotNull(categoryDTOList);
//        assertEquals(0, categoryDTOList.size());
//    }
//
//    @Test
//    public void TestCreateCategory_WIthValidCategoryDetails_RunsSuccessfully(){
//        // Arrange
//        long categoryId = 1L;
//        Category category = Category.builder().name("Test Category").description("Test Description").build();
//        Category categoryExpected = Category.builder().id(categoryId).name("Test Category").description("Test Description").build();
//        when(categoryService.createCategory(any(Category.class))).thenReturn(categoryExpected);
//        // Act
//        CategoryDTO categoryDTO = categoryController.createCategory(CategoryMapper.toCategoryDTO(category));
//        // Assert
//        assertNotNull(categoryDTO);
//        assertEquals(categoryId, categoryDTO.getId());
//        assertEquals("Test Category", categoryDTO.getName());
//        assertEquals("Test Description", categoryDTO.getDescription());
//        verify(categoryService, times(1)).createCategory(any(Category.class));
//    }
//
//    @Test
//    public void TestCreateCategory_WithNullCategoryDetails_ThrowsIllegalArgumentException() {
//        // Act and Assert
//        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, ()->categoryController.createCategory(null));
//        assertEquals("Category cannot be null", illegalArgumentException.getMessage());
//        verify(categoryService, times(1)).createCategory(any(Category.class));
//    }
//
//    @Test
//    public void TestCreateCategory_WithNullCategoryName_ThrowsIllegalArgumentException() {
//        // Act and Assert
//        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, ()->categoryController.createCategory(new CategoryDTO()));
//        assertEquals("Category name cannot be null or empty", illegalArgumentException.getMessage());
//        verify(categoryService, times(1)).createCategory(any(Category.class));
//    }
//
//    @Test
//    public void TestCreateCategory_WithEmptyCategoryName_ThrowsIllegalArgumentException() {
//        // Arrange
//        CategoryDTO categoryDTO = new CategoryDTO();
//        categoryDTO.setName("");
//        // Act and Assert
//        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, ()->categoryController.createCategory(categoryDTO));
//        assertEquals("Category name cannot be null or empty", illegalArgumentException.getMessage());
//        verify(categoryService, times(1)).createCategory(any(Category.class));
//    }
//
//    @Test
//    public void TestReplaceCategory_WithValidIdAndCategoryDetails_RunsSuccessfully(){
////       // Arrange
//        long categoryId = 1L;
//        Category category = Category.builder().id(categoryId).name("Test Category update").description("Test Description update").build();
//        when(categoryService.replaceCategoryByID(anyLong(), any())).thenReturn(category);
//        // Act
//        CategoryDTO categoryDTO = categoryController.replaceCategory(categoryId, CategoryMapper.toCategoryDTO(category));
//        // Assert
//        assertNotNull(categoryDTO);
//        assertEquals(categoryId, categoryDTO.getId());
//        assertEquals("Test Category update", categoryDTO.getName());
//        assertEquals("Test Description update", categoryDTO.getDescription());
//        verify(categoryService, times(1)).replaceCategoryByID(anyLong(), any(Category.class));
//    }
//    @Test
//    public void TestReplaceCategory_WithInvalidId_ThrowsIllegalArgumentException() {
//        // Act and Assert
//        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, ()->categoryController.replaceCategory(-1L, new CategoryDTO()));
//        assertEquals("Category id cannot be less than 1", illegalArgumentException.getMessage());
//    }
//
//    @Test
//    public void TestReplaceCategory_WithNullCategory_ThrowsIllegalArgumentException() {
//        // Act and Assert
//        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, ()->categoryController.replaceCategory(1L, null));
//        assertEquals("Category id cannot be null", illegalArgumentException.getMessage());
//    }
//}