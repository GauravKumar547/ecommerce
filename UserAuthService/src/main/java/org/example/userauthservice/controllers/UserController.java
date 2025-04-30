package org.example.userauthservice.controllers;

import org.example.userauthservice.dtos.UserDto;
import org.example.userauthservice.utils.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface UserController {
    ResponseEntity<ApiResponse<UserDto>> getUserDetails(Long id);
}
