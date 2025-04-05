package org.example.userauthservice.controllers;

import org.example.userauthservice.dtos.LoginRequestDto;
import org.example.userauthservice.dtos.ResponseDto;
import org.example.userauthservice.dtos.UserDto;
import org.example.userauthservice.dtos.ValidateTokenDto;
import org.springframework.http.ResponseEntity;
import org.example.userauthservice.utils.ApiResponse;

public interface AuthController {
    ResponseEntity<ApiResponse<UserDto>> login(LoginRequestDto requestDto);
    ResponseEntity<ApiResponse<ResponseDto>> signup(UserDto requestDto);
    ResponseEntity<ApiResponse<ResponseDto>> logout(String cookie,long id);
    ResponseEntity<ApiResponse<ResponseDto>> validateToken(ValidateTokenDto requestDto);
}