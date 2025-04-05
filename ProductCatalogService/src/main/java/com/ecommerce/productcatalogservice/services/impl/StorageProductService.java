package com.ecommerce.productcatalogservice.services.impl;

import com.ecommerce.productcatalogservice.models.Category;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.models.State;
import com.ecommerce.productcatalogservice.repos.CategoryRepository;
import com.ecommerce.productcatalogservice.repos.ProductRepository;
import com.ecommerce.productcatalogservice.services.IProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service("sqlProductService")
public class StorageProductService implements IProductService {
    private final CategoryRepository categoryRepository;
    ProductRepository productRepository;

    @Autowired
    public StorageProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Product getProductByID(long productId) {
        Optional<Product> product  = productRepository.findById(productId);
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
}