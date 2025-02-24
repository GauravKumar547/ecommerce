package com.ecommerce.productcatalogservice.services.impl;

import com.ecommerce.productcatalogservice.dtos.FakeStoreProductDTO;
import com.ecommerce.productcatalogservice.mappers.ProductMapper;
import com.ecommerce.productcatalogservice.models.Product;
import com.ecommerce.productcatalogservice.services.IProductService;
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

@Service
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
    public Product getProductByID(long productId) throws IllegalArgumentException {
        if(productId<1||productId>20){
            throw new IllegalArgumentException("Invalid product ID");
        }
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<FakeStoreProductDTO> prod = restTemplate.getForEntity("https://fakestoreapi.com/products/{id}", FakeStoreProductDTO.class, productId);
        if(prod.getBody()!=null&&prod.getStatusCode().equals(HttpStatus.OK)) {
            return ProductMapper.toProduct(prod.getBody());
        }
        return null;
    }

    @Override
    public List<Product> getAllProducts() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<FakeStoreProductDTO[]> products = restTemplate.getForEntity("https://fakestoreapi.com/products", FakeStoreProductDTO[].class);
        if(products.getBody()!=null && products.getStatusCode().equals(HttpStatus.OK)) {
            List<Product> productsList= new ArrayList<>(products.getBody().length);
            for(FakeStoreProductDTO dto: products.getBody()){
                productsList.add(ProductMapper.toProduct(dto));
            }
            return productsList;
        }
        return List.of();
    }

    @Override
    public Product replaceProductByID(long productId, Product product) {
        ResponseEntity<FakeStoreProductDTO> updatdProduct =  this.requestForEntity("https://fakestoreapi.com/products/{id}",HttpMethod.PUT,ProductMapper.toFakeStoreDTO(product),FakeStoreProductDTO.class,productId);
        if(updatdProduct.getBody()!=null && updatdProduct.getStatusCode().equals(HttpStatus.OK)) {
            return ProductMapper.toProduct(updatdProduct.getBody());
        }
        return null;
    }
}
