package com.ecommerce.productcatalogservice.services.impl;

import com.ecommerce.productcatalogservice.dtos.UserDto;
import com.ecommerce.productcatalogservice.models.Category;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.models.ProductStatus;
import com.ecommerce.productcatalogservice.models.State;
import com.ecommerce.productcatalogservice.repos.CategoryRepository;
import com.ecommerce.productcatalogservice.repos.ProductRepository;
import com.ecommerce.productcatalogservice.services.IProductService;
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
    ProductRepository productRepository;
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
        Optional<Product> product  = productRepository.findById(productId);
        product.ifPresent(value -> redisTemplate.opsForHash().put("products", productId, value));
        return product.orElse(null);
    }

    @Override
    @Transactional
    public List<Product> getAllProducts() {
        return productRepository.findAllByState(State.ACTIVE);
    }

    @Override
    @Transactional
    public Product replaceProductByID(long productId, Product product) {
        Optional<Product> oldProduct = productRepository.findById(productId);
        Optional<Category> categoryOptional = categoryRepository.findById(product.getCategory().getId());
        if(oldProduct.isPresent()){
            product.setState(oldProduct.get().getState());
            product.setId(productId);
            categoryOptional.ifPresent(product::setCategory);
            return productRepository.save(product);
        }
        return null;
    }

    @Override
    @Transactional
    public Boolean deleteProductByID(long productId) {
        Optional<Product> oldProduct = productRepository.findById(productId);
        if(oldProduct.isPresent()){
            Product product = oldProduct.get();
            product.setState(State.DELETED);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        if(product==null){
            throw new IllegalArgumentException("Product cannot be null");
        }else if(product.getId()>0 && productRepository.existsById(product.getId())){
            throw new IllegalArgumentException("Product with given id already exists");
        }
        Optional<Category> categoryOptional = categoryRepository.findById(product.getCategory().getId());
        categoryOptional.ifPresent(product::setCategory);
        return productRepository.save(product);
    }

    @Override
    public Product getProductByUserScope(Long productId, Long userId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if(productOptional.isEmpty()){
            throw new IllegalArgumentException("Product not found");
        }
        Product product = productOptional.get();
        if(product.getStatus().equals(ProductStatus.LISTED)){
            return  product;
        }

        ResponseEntity<UserDto> userResponse = restTemplate.getForEntity("http://user-auth-service/users/{userId}", UserDto.class, userId);
        if(userResponse.getStatusCode().equals(HttpStatus.OK) && userResponse.hasBody()){
            UserDto user = userResponse.getBody();

            if(user!=null && user.getRole().equals("ADMIN")){
                return product;
            }
        }

        return null;
    }
}