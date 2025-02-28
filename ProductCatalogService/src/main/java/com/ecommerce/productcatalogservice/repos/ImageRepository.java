package com.ecommerce.productcatalogservice.repos;

import com.ecommerce.productcatalogservice.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
