package com.ecommerce.userauthservice.controllers;

import com.ecommerce.commons.utils.response.ApiResponse;
import com.ecommerce.userauthservice.dtos.*;
import org.springframework.http.ResponseEntity;

public interface AuthController {
    ResponseEntity<ApiResponse<AuthResponse>> login(LoginRequestDto requestDto);
    ResponseEntity<ApiResponse<ResponseDto>> signup(UserDto userDto);
    ResponseEntity<ApiResponse<ResponseDto>> validateToken(ValidateTokenDto validateTokenDto);
    ResponseEntity<ApiResponse<ResponseDto>> logout(ValidateTokenDto logoutDto);
}