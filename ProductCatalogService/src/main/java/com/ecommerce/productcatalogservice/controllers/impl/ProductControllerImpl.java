package com.ecommerce.productcatalogservice.controllers.impl;

import com.ecommerce.productcatalogservice.controllers.ProductController;
import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import com.ecommerce.productcatalogservice.dtos.ResponseDTO;
import com.ecommerce.productcatalogservice.mappers.ImageMapper;
import com.ecommerce.productcatalogservice.mappers.ProductMapper;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.services.IProductService;
import com.ecommerce.productcatalogservice.utils.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductControllerImpl implements ProductController {
    private final IProductService productService;

    @Autowired
    public ProductControllerImpl(@Qualifier("sqlProductService") IProductService productService) {
        this.productService = productService;
    }


    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<ProductDTO>> addProduct(@RequestBody ProductDTO product) {
        if(product == null) {
           throw new IllegalArgumentException("Product cannot be null");
        }else if(product.getCategory() == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        Product productResponse = productService.createProduct(ProductMapper.toProduct(product));
        ApiResponse<ProductDTO> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(HttpStatus.CREATED).setData(ProductMapper.toProductDTO(productResponse));
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ResponseDTO>> deleteProduct(@PathVariable long id) {
        if(id<1){
            throw new IllegalArgumentException("Product id must be greater than 0");
        }
        ResponseDTO responseDTO = new ResponseDTO();
        ApiResponse<ResponseDTO> apiResponse = new ApiResponse<>();
        if(productService.deleteProductByID(id)){
            apiResponse.setData(responseDTO.setMessage("Delete product successful")).setStatus(HttpStatus.OK);
        }else{
            apiResponse.setData(responseDTO.setMessage("Delete product failed")).setStatus(HttpStatus.NOT_FOUND);
        }

        return ApiResponse.getResponseEntity(apiResponse);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> replaceProduct(@PathVariable long id, @RequestBody ProductDTO product) {
        if(id<1){
            throw new IllegalArgumentException("Product id must be greater than 0");
        } else if(product==null){
            throw new IllegalArgumentException("Product cannot be null");
        }
        Product productResponse = productService.replaceProductByID(id,ProductMapper.toProduct(product));
        ApiResponse<ProductDTO> apiResponse = new ApiResponse<>();
        if(productResponse!=null){
            apiResponse.setData(ProductMapper.toProductDTO(productResponse)).setStatus(HttpStatus.OK);
        }else{
            apiResponse.setError("Replace product not successful").setStatus(HttpStatus.NOT_FOUND);
        }
        return  ApiResponse.getResponseEntity(apiResponse);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProduct(@PathVariable long id) {
        if(id<1){
            throw new IllegalArgumentException("Product id must be greater than 0");
        }
        Product product = productService.getProductByID(id);
        ApiResponse<ProductDTO> apiResponse = new ApiResponse<>();
        if (product == null) {
            apiResponse.setError("Product not found").setStatus(HttpStatus.NOT_FOUND);
        }else{
            apiResponse.setData(ProductMapper.toProductDTO(product)).setStatus(HttpStatus.OK);
        }
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
        ApiResponse<List<ProductDTO>> apiResponse = new ApiResponse<>();
        List<Product> products = productService.getAllProducts();
        List<ProductDTO> productDTOList = products.stream().map(ProductMapper::toProductDTO).toList();
        apiResponse.setData(productDTOList).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @Override
    @GetMapping("category/{categoryName}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByCategory(@PathVariable String categoryName) {
        if(categoryName.trim().isEmpty()){
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        ApiResponse<List<ProductDTO>> apiResponse = new ApiResponse<>();
        List<Product> products = productService.getAllProducts();
        List<ProductDTO> productDTOList = products.stream().filter(product -> product.getCategory()!=null&&product.getCategory().getName().equals(categoryName)).map(ProductMapper::toProductDTO).toList();
        apiResponse.setData(productDTOList).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@PathVariable long id, @RequestBody ProductDTO product) {
        if(id<1){
            throw new IllegalArgumentException("Product id must be greater than 0");
        } else if(product==null){
            throw new IllegalArgumentException("Updating data of product cannot be null");
        }
        Product product1 = productService.getProductByID(id);
        if (product1 == null) {
            throw new IllegalArgumentException("Product with given id not found");
        }
        if(product.getName()!=null) {
            product1.setName(product.getName());
        }
        if(product.getPrice()>0) {
            product1.setPrice(product.getPrice());
        }
        if(product.getDescription()!=null) {
            product1.setDescription(product.getDescription());
        }
        if(product.getImages()!=null&&!product.getImages().isEmpty()) {
            product1.setImages(product.getImages().stream().map(ImageMapper::
            toImage).collect(Collectors.toList()));
        }
        ProductDTO productDTO = ProductMapper.toProductDTO(productService.replaceProductByID(id, product1));
        ApiResponse<ProductDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(productDTO).setStatus(HttpStatus.OK);

        return  ApiResponse.getResponseEntity(apiResponse);
    }
}