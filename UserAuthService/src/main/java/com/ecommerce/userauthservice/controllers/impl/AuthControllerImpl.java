package com.ecommerce.userauthservice.controllers.impl;

import com.ecommerce.commons.utils.response.ApiResponse;
import com.ecommerce.userauthservice.dtos.*;
import com.ecommerce.userauthservice.exceptions.UserAlreadyExistingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.antlr.v4.runtime.misc.Pair;
import com.ecommerce.userauthservice.controllers.AuthController;
import com.ecommerce.userauthservice.mappers.UserMapper;
import com.ecommerce.userauthservice.models.User;
import com.ecommerce.userauthservice.services.impl.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "APIs for user authentication")
public class AuthControllerImpl implements AuthController {
    private final AuthServiceImpl authService;

    @Autowired
    public AuthControllerImpl(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns access token")
    @Override
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequestDto requestDto) {
        if (requestDto.getEmail() == null || requestDto.getPassword() == null) {
            throw new IllegalArgumentException("Email and password are required");
        }
        
        ApiResponse<AuthResponse> apiResponse = new ApiResponse<>();
        Pair<User, String> loginResult = authService.login(requestDto.getEmail(), requestDto.getPassword());

        AuthResponse authResponse = new AuthResponse(
            loginResult.a.getId(),
            loginResult.b
        );
        
        apiResponse.setData(authResponse).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/signup")
    @Operation(summary = "User signup", description = "Register a new user")
    @Override
    public ResponseEntity<ApiResponse<ResponseDto>> signup(@RequestBody UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getPassword() == null || userDto.getName() == null) {
            throw new IllegalArgumentException("Name, email and password are required");
        }

        User user = UserMapper.toUser(userDto);
        Boolean isCreated = authService.signup(user);
        
        ApiResponse<ResponseDto> apiResponse = new ApiResponse<>();
        if (!isCreated) {
           throw  new UserAlreadyExistingException("User with this email already exists");
        }else{
            ResponseDto responseDto = new ResponseDto();
            responseDto.setMessage("User registered successfully");
            apiResponse.setData(responseDto).setStatus(HttpStatus.CREATED);
        }
        
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate a user's token")
    @Override
    public ResponseEntity<ApiResponse<ResponseDto>> validateToken(@RequestBody ValidateTokenDto validateTokenDto) {
        if (validateTokenDto.getToken() == null) {
            throw new IllegalArgumentException("Token is required");
        }
        
        Boolean isValid = authService.validateToken(validateTokenDto.getToken(), validateTokenDto.getUserId());
        
        ApiResponse<ResponseDto> apiResponse = new ApiResponse<>();
        if (!isValid) {
            apiResponse.setError("Invalid token").setStatus(HttpStatus.UNAUTHORIZED);
            return ApiResponse.getResponseEntity(apiResponse);
        }   else {
            ResponseDto responseDto = new ResponseDto();
            responseDto.setMessage("Token is valid");
            apiResponse.setData(responseDto).setStatus(HttpStatus.OK);
        }
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout a user and invalidate their token")
    @Override
    public ResponseEntity<ApiResponse<ResponseDto>> logout(@RequestBody ValidateTokenDto logoutDto) {
        if (logoutDto.getToken() == null) {
            throw new IllegalArgumentException("Token is required");
        }
        
        authService.logout(logoutDto.getToken(), logoutDto.getUserId());
        
        ApiResponse<ResponseDto> apiResponse = new ApiResponse<>();
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Logged out successfully");
        apiResponse.setData(responseDto).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }
}