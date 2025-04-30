package com.ecommerce.productcatalogservice.mappers;

import com.ecommerce.productcatalogservice.dtos.FakeStoreProductDTO;
import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import com.ecommerce.productcatalogservice.models.Category;
import com.ecommerce.productcatalogservice.models.Image;
import com.ecommerce.productcatalogservice.models.Product;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductMapper {
    static public ProductDTO toProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setCategory(CategoryMapper.toCategoryDTO(product.getCategory()));
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setImages(Optional.ofNullable(product.getImages()).orElse(Collections.emptyList()).stream().map(ImageMapper::toImageDTO).collect(Collectors.toList()));
        productDTO.setDescription(product.getDescription());
        productDTO.setStatus(product.getStatus());

        return productDTO;
    }
    static public Product toProduct(ProductDTO productDTO) {
        return Product.builder()
                .id(productDTO.getId())
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .images(Optional.ofNullable(productDTO.getImages()).orElse(Collections.emptyList()).stream().map(ImageMapper::toImage).collect(Collectors.toList()))
                .description(productDTO.getDescription())
                .category(CategoryMapper.toCategory(productDTO.getCategory()))
                .state(productDTO.getState())
                .status(productDTO.getStatus())
                .build();
    }
    static public Product toProduct(FakeStoreProductDTO productDTO) {
        return Product.builder()
                .id(productDTO.getId())
                .name(productDTO.getTitle())
                .price(productDTO.getPrice())
                .images(List.of(Image.builder().url(productDTO.getImage()).build()))
                .description(productDTO.getDescription())
                .category(Category.builder().id(0).name(productDTO.getCategory()).build())
                .build();
    }
    static public FakeStoreProductDTO toFakeStoreDTO(Product product){
        return FakeStoreProductDTO.builder()
                .id(product.getId())
                .title(product.getName())
                .image(product.getImages().getFirst().getUrl())
                .price(product.getPrice())
                .description(product.getDescription())
                .category(product.getCategory().getName())
                .build();
    }
}
