package com.ecommerce.productcatalogservice.services.impl;

import com.ecommerce.productcatalogservice.dtos.UserDto;
import com.ecommerce.productcatalogservice.models.Category;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.models.ProductStatus;
import com.ecommerce.productcatalogservice.models.State;
import com.ecommerce.productcatalogservice.repos.CategoryRepository;
import com.ecommerce.productcatalogservice.repos.ProductRepository;
import com.ecommerce.productcatalogservice.services.IProductService;
import com.ecommerce.commons.exceptions.ResourceNotFoundException;
import com.ecommerce.commons.exceptions.BadRequestException;
import com.ecommerce.commons.exceptions.UnauthorizedException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
@Service("sqlProductService")
public class StorageProductService implements IProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate;

    @Autowired
    public StorageProductService(ProductRepository productRepository, CategoryRepository categoryRepository, RedisTemplate<String, Object> redisTemplate, RestTemplate restTemplate) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional
    public Product getProductByID(long productId) {
        Product cachedProduct = (Product) redisTemplate.opsForHash().get("products", productId);
        if (cachedProduct != null) {
            return cachedProduct;
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        redisTemplate.opsForHash().put("products", productId, product);
        return product;
    }

    @Override
    @Transactional
    public List<Product> getAllProducts() {
        return productRepository.findAllByState(State.ACTIVE);
    }

    @Override
    @Transactional
    public Product replaceProductByID(long productId, Product product) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", product.getCategory().getId()));

        product.setState(existingProduct.getState());
        product.setId(productId);
        product.setCategory(category);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Boolean deleteProductByID(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        product.setState(State.DELETED);
        productRepository.save(product);
        return true;
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        if (product == null) {
            throw new BadRequestException("Product cannot be null");
        }
        if (product.getId() > 0 && productRepository.existsById(product.getId())) {
            throw new BadRequestException("Product with given id already exists");
        }
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", product.getCategory().getId()));
        product.setCategory(category);
        return productRepository.save(product);
    }

    @Override
    public Product getProductByUserScope(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (product.getStatus().equals(ProductStatus.LISTED)) {
            return product;
        }

        ResponseEntity<UserDto> userResponse = restTemplate.getForEntity("http://user-auth-service/users/{userId}", UserDto.class, userId);
        if (userResponse.getStatusCode().equals(HttpStatus.OK) && userResponse.hasBody()) {
            UserDto user = userResponse.getBody();
            if (user != null && user.getRole().equals("ADMIN")) {
                return product;
            }
        }

        throw new UnauthorizedException("User not authorized to access this product");
    }
}