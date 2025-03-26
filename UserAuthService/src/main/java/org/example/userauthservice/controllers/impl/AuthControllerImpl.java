package org.example.userauthservice.controllers.impl;

import org.example.userauthservice.controllers.AuthController;
import org.example.userauthservice.dtos.LoginRequestDto;
import org.example.userauthservice.dtos.ResponseDto;
import org.example.userauthservice.dtos.UserDto;
import org.example.userauthservice.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.userauthservice.utils.ApiResponse;

@RestController
@RequestMapping("/auth")
public class AuthControllerImpl implements AuthController {
    @Override
    public ResponseEntity<ApiResponse<UserDto>> login(LoginRequestDto requestDto) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse<ResponseDto>> signup(User requestDto) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse<ResponseDto>> logout() {
        return null;
    }
}