package com.ecommerce.productcatalogservice.services.impl;

import com.ecommerce.productcatalogservice.dtos.FakeStoreProductDTO;
import com.ecommerce.productcatalogservice.mappers.ProductMapper;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.services.IProductService;
import com.ecommerce.commons.exceptions.ResourceNotFoundException;
import com.ecommerce.commons.exceptions.BadRequestException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Service("fsProductService")
public class FakeStoreProductService implements IProductService {

    private final RestTemplateBuilder restTemplateBuilder;
    public FakeStoreProductService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    public <T> ResponseEntity<T> requestForEntity(String url,HttpMethod requestMethod, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        RestTemplate restTemplate = restTemplateBuilder.build();
        RequestCallback requestCallback = restTemplate.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);
        return restTemplate.execute(url, requestMethod, requestCallback, responseExtractor, uriVariables);
    }
    @Override
    public Product getProductByID(long productId) {
        if (productId < 1 || productId > 20) {
            throw new BadRequestException("Invalid product ID. ID must be between 1 and 20");
        }
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<FakeStoreProductDTO> response = restTemplate.getForEntity(
                "https://fakestoreapi.com/products/{id}",
                FakeStoreProductDTO.class,
                productId
        );
        if (response.getBody() != null && response.getStatusCode().equals(HttpStatus.OK)) {
            return ProductMapper.toProduct(response.getBody());
        }
        throw new ResourceNotFoundException("Product", "id", productId);
    }

    @Override
    public List<Product> getAllProducts() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<FakeStoreProductDTO[]> response = restTemplate.getForEntity(
                "https://fakestoreapi.com/products",
                FakeStoreProductDTO[].class
        );
        List<Product> products = new ArrayList<>();
        if (response.getBody() != null && response.getStatusCode().equals(HttpStatus.OK)) {
            for (FakeStoreProductDTO dto : response.getBody()) {
                products.add(ProductMapper.toProduct(dto));
            }
        }
        return products;
    }

    @Override
    public Product replaceProductByID(long productId, Product product) {
        if (productId < 1 || productId > 20) {
            throw new BadRequestException("Invalid product ID. ID must be between 1 and 20");
        }
        if (product == null) {
            throw new BadRequestException("Product cannot be null");
        }
        
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<FakeStoreProductDTO> response = restTemplate.exchange(
                "https://fakestoreapi.com/products/{id}",
                HttpMethod.PUT,
                null,
                FakeStoreProductDTO.class,
                productId
        );
        if (response.getBody() != null && response.getStatusCode().equals(HttpStatus.OK)) {
            return ProductMapper.toProduct(response.getBody());
        }
        throw new ResourceNotFoundException("Product", "id", productId);
    }

    @Override
    public Boolean deleteProductByID(long productId) {
        if (productId < 1 || productId > 20) {
            throw new BadRequestException("Invalid product ID. ID must be between 1 and 20");
        }
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<Void> response = restTemplate.exchange(
                "https://fakestoreapi.com/products/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                productId
        );
        return response.getStatusCode().equals(HttpStatus.OK);
    }

    @Override
    public Product createProduct(Product product) {
        if (product == null) {
            throw new BadRequestException("Product cannot be null");
        }
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<FakeStoreProductDTO> response = restTemplate.postForEntity(
                "https://fakestoreapi.com/products",
                product,
                FakeStoreProductDTO.class
        );
        if (response.getBody() != null && response.getStatusCode().equals(HttpStatus.CREATED)) {
            return ProductMapper.toProduct(response.getBody());
        }
        throw new BadRequestException("Failed to create product");
    }

    @Override
    public Product getProductByUserScope(Long productId, Long userId) {
        throw new UnsupportedOperationException("User scope not supported in FakeStore implementation");
    }
}
