//package com.ecommerce.productcatalogservice.services;
//
//import com.ecommerce.productcatalogservice.controllers.ProductController;
//import com.ecommerce.productcatalogservice.dtos.ProductDTO;
//import com.ecommerce.productcatalogservice.dtos.ResponseDTO;
//import com.ecommerce.productcatalogservice.mappers.ProductMapper;
//import com.ecommerce.productcatalogservice.models.Category;
//import com.ecommerce.productcatalogservice.models.Product;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//public class StorageProductServiceTest {
//    @MockitoBean
//    @Qualifier("sqlProductService")
//    private IProductService productService;
//
//    @Autowired
//    private ProductController productController;
//
//    @Captor
//    private ArgumentCaptor<Long> idCaptor;
//
//    @Test
//    public void TestGetProduct_WithValidId_RunsSuccessfully() {
//        // Arrange
//        long id = 3L;
//        Product product = new Product();
//        product.setId(id);
//        when(productService.getProductByID(id)).thenReturn(product);
//
//        // Act
//        ProductDTO productDTO = productController.getProduct(id);
//
//        // Assert
//        assertNotNull(productDTO);
//        assertEquals(id, productDTO.getId());
//    }
//
//    @Test
//    public void TestGetProduct_WithNegativeId_ThrowsIllegalArgumentException() {
//        // Arrange
//        long id = -1L;
//
//        // Act and Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->productController.getProduct(id));
//        assertEquals("Product id must be greater than 0", exception.getMessage());
//    }
//
//    @Test
//    public void TestGetProduct_WithUnavailableId_ReturnsNull(){
//        // Arrange
//        long id = 4L;
//
//        // Act
//        ProductDTO productDTO = productController.getProduct(id);
//
//        // Assert
//        assertNull(productDTO);
//    }
//    @Test
//    public void TestGetProduct_ProductServiceCalledWithCorrectArguments_RunsSuccessfully() {
//        // Arrange
//        long productId = 1L;
//        Product product = new Product();
//        product.setId(productId);
//        product.setName("TestProd");
//        when(productService.getProductByID(productId)).thenReturn(product);
//
//        // Act
//        productController.getProduct(productId);
//
//        // Assert
//        verify(productService).getProductByID(idCaptor.capture());
//        assertEquals(productId, idCaptor.getValue());
//    }
//
//    @Test
//    public void TestAddProduct_withCompleteValidProduct_RunsSuccessfully(){
//        // Arrange
//        Long productId = 1L;
//        Category category = Category.builder().name("test").id(1L).build();
//        Product product = Product.builder().name("TestProd").category(category).description("testing desc for product").build();
//        Product productExpected = Product.builder().id(productId).name("TestProd").category(category).description("testing desc for product").build();
//        when(productService.createProduct(any(Product.class))).thenReturn(productExpected);
//
//        // Act
//        ProductDTO productDTO =  productController.addProduct(ProductMapper.toProductDTO(product));
//
//        // Assert
//        assertNotNull(productDTO);
//        assertEquals(productId, productDTO.getId());
//        assertEquals(product.getName(), productDTO.getName());
//        assertEquals(product.getDescription(), productDTO.getDescription());
//        assertNotNull(productDTO.getCategory());
//        assertEquals(product.getCategory().getId(), productDTO.getCategory().getId());
//        assertEquals(product.getCategory().getName(), productDTO.getCategory().getName());
//        assertEquals(product.getCategory().getDescription(), productDTO.getCategory().getDescription());
//        verify(productService, times(1)).createProduct(any(Product.class));
//    }
//
//    @Test
//    public void TestAddProduct_WithNullProduct_ThrowsIllegalArgumentException() {
//        // Act and Assert
//        IllegalArgumentException Exception =  assertThrows(IllegalArgumentException.class,()->productController.addProduct(null));
//        assertEquals("Product cannot be null", Exception.getMessage());
//    }
//
//    @Test
//    public void TestAddProduct_WithNullCategory_ThrowsIllegalArgumentException() {
//        // Act and Assert
//        IllegalArgumentException Exception =  assertThrows(IllegalArgumentException.class,()->productController.addProduct(new ProductDTO()));
//        assertEquals("Category cannot be null", Exception.getMessage());
//    }
//
//    @Test
//    public void TestDeleteProduct_WithValidId_RunsSuccessfully() {
//        // Arrange
//        long productId = 1L;
//        when(productService.deleteProductByID(anyLong())).thenReturn(true);
//
//        // Act
//        ResponseDTO responseDTO =  productController.deleteProduct(productId);
//
//        // Assert
//        assertEquals("Delete product successful", responseDTO.getMessage());
//        verify(productService, times(1)).deleteProductByID(anyLong());
//    }
//
//    @Test
//    public void TestDeleteProduct_WithNegativeId_ThrowsIllegalArgumentException() {
//        // Act and Assert
//        IllegalArgumentException Exception =  assertThrows(IllegalArgumentException.class,()->productController.deleteProduct(-1L));
//        assertEquals("Product id must be greater than 0", Exception.getMessage());
//    }
//
//    @Test
//    public void TestDeleteProduct_WithInValidId_FailsSuccessfully() {
//        // Arrange
//        long productId = 5L;
//        when(productService.deleteProductByID(anyLong())).thenReturn(false);
//
//        // Act
//        ResponseDTO responseDTO =  productController.deleteProduct(productId);
//
//        // Assert
//        assertEquals("Delete product failed", responseDTO.getMessage());
//        verify(productService, times(1)).deleteProductByID(anyLong());
//    }
//
//    @Test
//    public void TestReplaceProduct_WithValidId_RunsSuccessfully() {
//        // Arrange
//        long productId = 1L;
//        Category category = Category.builder().name("test").id(1L).build();
//        Product product = Product.builder().id(productId).name("TestProd").category(category).description("testing " +
//                "desc updated for product").build();
//        Product productExpected = Product.builder().id(productId).name("TestProd123").category(category).description(
//                "testing desc for product").build();
//        when(productService.replaceProductByID(anyLong(), any(Product.class))).thenReturn(productExpected);
//
//        // Act
//        ProductDTO productDTO = productController.replaceProduct(productId,ProductMapper.toProductDTO(productExpected));
//
//        // Assert
//        assertNotNull(productDTO);
//        assertNotEquals(product.getName(), productDTO.getName());
//        assertNotEquals(product.getDescription(), productDTO.getDescription());
//        assertEquals(productExpected.getName(), productDTO.getName());
//        assertEquals(productExpected.getDescription(), productDTO.getDescription());
//        verify(productService, times(1)).replaceProductByID(anyLong(),any(Product.class));
//    }
//
//    @Test
//    public void TestReplaceProduct_WithInvalidId_ThrowsIllegalArgumentException() {
//        // Act and Assert
//        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
//                ()-> productController.replaceProduct(-1L,new ProductDTO()));
//        assertEquals("Product id must be greater than 0", illegalArgumentException.getMessage());
//    }
//    @Test
//    public void TestReplaceProduct_WithNullProduct_ThrowsIllegalArgumentException() {
//        // Act and Assert
//        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
//                ()-> productController.replaceProduct(1L,null));
//        assertEquals("Product cannot be null", illegalArgumentException.getMessage());
//    }
//
//    @Test
//    public void TestReplaceProduct_WithServiceThrowingException_ThrowsIllegalArgumentException() {
//        // Arrange
//        when(productService.replaceProductByID(anyLong(),any(Product.class))).thenThrow(new RuntimeException("Service" +
//                " error"));
//        // Act and Assert
//        RuntimeException runtimeException = assertThrows(RuntimeException.class,
//                ()-> productController.replaceProduct(1L,new ProductDTO()));
//        assertEquals("Service error", runtimeException.getMessage());
//    }
//
//    @Test
//    public void TestGetAllProducts_ReturnProductList() {
//        // Arrange
//        when(productService.getAllProducts()).thenReturn(List.of(new Product(), new Product(), new Product()));
//
//        // Act
//        List<ProductDTO> productDTOList = productController.getAllProducts();
//
//        // Assert
//        assertNotNull(productDTOList);
//        assertEquals(3, productDTOList.size());
//    }
//
//    @Test
//    public void TestGetAllProducts_ReturnNull() {
//        // Arrange
//        when(productService.getAllProducts()).thenReturn(Collections.emptyList());
//
//        // Act
//        List<ProductDTO> productDTOList = productController.getAllProducts();
//
//        // Assert
//        assertNull(productDTOList);
//    }
//
//    @Test
//    public void TestGetProductByCategory_WithValidCategoryName_RunsSuccessfully() {
//        // Arrange
//        String categoryName = "test";
//        Category category = Category.builder().name(categoryName).id(1L).build();
//        List<Product> products = List.of(Product.builder().category(category).build(), new Product(), new Product());
//        when(productService.getAllProducts()).thenReturn(products);
//
//        // Act
//        List<ProductDTO> productDTOList = productController.getProductsByCategory(categoryName);
//
//        // Assert
//        assertNotNull(productDTOList);
//        assertEquals(1, productDTOList.size());
//        verify(productService, times(1)).getAllProducts();
//    }
//
//
//    @Test
//    public void TestGetProductByCategory_WithNoProductForCategory_ReturnsEmptyList() {
//        // Arrange
//        String categoryName = "test";
//        Category category = Category.builder().name("test23").id(1L).build();
//        List<Product> products = List.of(Product.builder().category(category).build(),
//                Product.builder().category(category).build(), new Product());
//        when(productService.getAllProducts()).thenReturn(products);
//
//        // Act
//        List<ProductDTO> productDTOList = productController.getProductsByCategory(categoryName);
//
//        // Assert
//        assertNotNull(productDTOList);
//        assertEquals(0, productDTOList.size());
//        verify(productService, times(1)).getAllProducts();
//    }
//
//    @Test
//    public void TestGetProductByCategory_WithNullCategoryName_ThrowsIllegalArgumentException() {
//        // Act and Assert
//        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
//                ()->productController.getProductsByCategory(null));
//        assertEquals("Category name cannot be null", illegalArgumentException.getMessage());
//    }
//
//    @Test
//    public void TestGetProductByCategory_WithInvalidCategoryName_ThrowsIllegalArgumentException() {
//        // Act and Assert
//        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
//                ()->productController.getProductsByCategory(""));
//        assertEquals("Category name cannot be empty", illegalArgumentException.getMessage());
//    }
//
//    @Test
//    public void TestUpdateProduct_WithValidIdAndProduct_RunsSuccessfully() {
//        long productId = 1L;
//        Category category = Category.builder().name("test").id(1L).build();
//        Product product = Product.builder().id(productId).name("TestProd").category(category).description("testing " +
//                "desc updated for product").build();
//        Product productExpected = Product.builder().id(productId).name("TestProd123").category(category).description(
//                "testing desc for product").build();
//        when(productService.replaceProductByID(anyLong(), any(Product.class))).thenReturn(productExpected);
//
//        // Act
//        ProductDTO productDTO = productController.replaceProduct(productId,ProductMapper.toProductDTO(productExpected));
//
//        // Assert
//        assertNotNull(productDTO);
//        assertNotEquals(product.getName(), productDTO.getName());
//        assertNotEquals(product.getDescription(), productDTO.getDescription());
//        assertEquals(productExpected.getName(), productDTO.getName());
//        assertEquals(productExpected.getDescription(), productDTO.getDescription());
//        verify(productService, times(1)).replaceProductByID(anyLong(),any(Product.class));
//    }
//    @Test
//    public void TestUpdateProduct_WithValidIdAndProductWithOneField_RunsSuccessfully() {
//        long productId = 1L;
//        Category category = Category.builder().name("test").id(1L).build();
//        Product product = Product.builder().id(productId).name("TestProd").category(category).description(
//                "testing desc for product").build();
//        Product productExpected = Product.builder().id(productId).name("TestProd123").category(category).description(
//                "testing desc for product").build();
//        ProductDTO updateFieldProduct = new ProductDTO();
//        updateFieldProduct.setName("TestProd123");
//        when(productService.replaceProductByID(anyLong(), any(Product.class))).thenReturn(productExpected);
//        when(productService.getProductByID(anyLong())).thenReturn(product);
//
//        // Act
//        ProductDTO productDTO = productController.updateProduct(productId,updateFieldProduct);
//
//        // Assert
//        assertNotNull(productDTO);
//        System.out.println(productDTO.getName());
//        System.out.println(product.getName());
//        System.out.println(productExpected.getName());
//        assertNotEquals("test", productDTO.getName());
//        assertEquals(productExpected.getName(), productDTO.getName());
//        assertEquals(product.getDescription(), productDTO.getDescription());
//        verify(productService, times(1)).replaceProductByID(anyLong(),any(Product.class));
//        verify(productService, times(1)).getProductByID(anyLong());
//    }
//
//    @Test
//    public void TestUpdateProduct_WIthInvalidId_ThrowsIllegalArgumentException() {
//        // Act and Assert
//        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, ()->productController.updateProduct(-1L, new ProductDTO()));
//        assertEquals("Product id must be greater than 0", illegalArgumentException.getMessage());
//    }
//
//    @Test
//    public void TestUpdateProduct_WithNullProduct_ThrowsIllegalArgumentException() {
//        // Act and Assert
//        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, ()->productController.updateProduct(1L, null));
//        assertEquals("Updating data of product cannot be null", illegalArgumentException.getMessage());
//    }
//    @Test
//    public void TestUpdateProduct_WithNonExistingProductId_ThrowsIllegalArgumentException() {
//        // Arrange
//        long id = 5L;
//        when(productService.getProductByID(anyLong())).thenReturn(null);
//
//        // Act and Assert
//        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,()-> productController.updateProduct(id, new ProductDTO()));
//        assertEquals("Product with given id not found", illegalArgumentException.getMessage());
//
//    }
//
//    @Test
//    public void TestUpdateProduct_WithValidIdAndProductWithEmptyFields_RunsSuccessfully(){
//        // Arrange
//        long productId = 1L;
//        Category category = Category.builder().name("test").id(1L).build();
//        Product product = Product.builder().id(productId).name("TestProd").category(category).description(
//                "testing desc for product").build();
//        when(productService.getProductByID(anyLong())).thenReturn(product);
//        when(productService.replaceProductByID(anyLong(), any(Product.class))).thenReturn(product);
//
//        // Act
//        ProductDTO productDTO= productController.updateProduct(productId,new ProductDTO());
//
//        // Assert
//        assertNotNull(productDTO);
//        assertEquals("TestProd", productDTO.getName());
//        assertEquals("testing desc for product", productDTO.getDescription());
//        verify(productService, times(1)).replaceProductByID(anyLong(),any(Product.class));
//        verify(productService, times(1)).getProductByID(anyLong());
//
//    }
//
//
//}