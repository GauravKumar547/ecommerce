package com.ecommerce.productcatalogservice.mappers;

import com.ecommerce.productcatalogservice.dtos.ImageDTO;
import com.ecommerce.productcatalogservice.models.Image;

public class ImageMapper {
    static public ImageDTO toImageDTO(Image image) {
    ImageDTO imageDTO = new ImageDTO();
    imageDTO.setId(image.getId());
    imageDTO.setUrl(image.getUrl());
    return imageDTO;
    }
    static public Image toImage(ImageDTO imageDTO) {
        return Image.builder().id(imageDTO.getId()).url(imageDTO.getUrl()).build();
    }
}
