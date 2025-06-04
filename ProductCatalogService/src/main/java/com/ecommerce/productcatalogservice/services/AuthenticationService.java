package com.ecommerce.productcatalogservice.services;

import com.ecommerce.productcatalogservice.dtos.UserDto;

public interface AuthenticationService {
    UserDto validateToken(String token);
    void invalidateToken(String token);
} 