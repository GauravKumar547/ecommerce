package com.ecommerce.productcatalogservice.mappers;

import com.ecommerce.productcatalogservice.dtos.ProductDTO;
import com.ecommerce.productcatalogservice.models.Product;

public class ProductMapper {
    static public ProductDTO toProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setCategory(CategoryMapper.toCategoryDTO(product.getCategory()));
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setImages(product.getImages());
        productDTO.setDescription(product.getDescription());

        return productDTO;
    }
    static public Product toProduct(ProductDTO productDTO) {
        return Product.builder()
                .id(productDTO.getId())
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .images(productDTO.getImages())
                .description(productDTO.getDescription())
                .category(CategoryMapper.toCategory(productDTO.getCategory()))
                .build();
    }
}
