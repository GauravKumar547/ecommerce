package com.ecommerce.userauthservice.controllers;

import com.ecommerce.commons.utils.response.ApiResponse;
import com.ecommerce.userauthservice.dtos.UserDto;
import org.springframework.http.ResponseEntity;

public interface UserController {
    ResponseEntity<ApiResponse<UserDto>> getUserDetails(Long id);
}