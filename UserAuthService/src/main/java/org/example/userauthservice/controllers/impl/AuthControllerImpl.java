package org.example.userauthservice.controllers.impl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthservice.controllers.AuthController;
import org.example.userauthservice.dtos.*;
import org.example.userauthservice.mappers.UserMapper;
import org.example.userauthservice.models.RefreshToken;
import org.example.userauthservice.models.User;
import org.example.userauthservice.services.impl.AuthServiceImpl;
import org.example.userauthservice.services.impl.RefreshTokenService;
import org.example.userauthservice.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "APIs for user authentication")
public class AuthControllerImpl implements AuthController {
    private final AuthServiceImpl authService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthControllerImpl(AuthServiceImpl authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns access and refresh tokens")
    @Override
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequestDto requestDto) {
        if (requestDto.getEmail() == null || requestDto.getPassword() == null) {
            throw new IllegalArgumentException("Email and password are required");
        }
        
        ApiResponse<AuthResponse> apiResponse = new ApiResponse<>();
        Pair<User, String> loginResult = authService.login(requestDto.getEmail(), requestDto.getPassword());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginResult.a.getId());
        
        AuthResponse authResponse = new AuthResponse(
            loginResult.a.getId(),
            loginResult.b,
            refreshToken.getToken()
        );
        
        apiResponse.setData(authResponse).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/signup")
    @Operation(summary = "User signup", description = "Registers a new user")
    @Override
    public ResponseEntity<ApiResponse<ResponseDto>> signup(@RequestBody UserDto requestDto) {
        if(requestDto.getEmail() == null || requestDto.getPassword() == null || requestDto.getName() == null
            || requestDto.getEmail().isEmpty() || requestDto.getPassword().isEmpty() || requestDto.getName().isEmpty()) {
            throw new IllegalArgumentException("Required parameters not provided");
        }
        
        ApiResponse<ResponseDto> apiResponse = new ApiResponse<>();
        if(authService.signup(UserMapper.toUser(requestDto))) {
            ResponseDto responseDto = new ResponseDto();
            responseDto.setMessage("User registered successfully");
            apiResponse.setStatus(HttpStatus.CREATED).setData(responseDto);
        }
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logs out a user and invalidates their tokens")
    @Override
    public ResponseEntity<ApiResponse<ResponseDto>> logout(@CookieValue(name = "token", required = false) String token, @RequestParam long id) {
        ApiResponse<ResponseDto> apiResponse = new ApiResponse<>();
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("No active session found");
        }
        
        authService.logout(token, id);
        refreshTokenService.deleteByUserId(id);
        
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Logged out successfully");
        apiResponse.setData(responseDto).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/validate_token")
    @Operation(summary = "Validate token", description = "Validates a user's token")
    @Override
    public ResponseEntity<ApiResponse<ResponseDto>> validateToken(@RequestBody ValidateTokenDto requestDto) {
        if (requestDto.getToken() == null || requestDto.getToken().isEmpty()) {
            throw new IllegalArgumentException("Token not provided");
        }
        if (requestDto.getUserId() <= 0) {
            throw new IllegalArgumentException("User id not provided");
        }
        
        ApiResponse<ResponseDto> apiResponse = new ApiResponse<>();
        ResponseDto responseDto = new ResponseDto();
        
        if(authService.validateToken(requestDto.getToken(), requestDto.getUserId())) {
            responseDto.setMessage("Token validated successfully");
            apiResponse.setStatus(HttpStatus.OK).setData(responseDto);
        } else {
            responseDto.setMessage("Invalid token");
            apiResponse.setStatus(HttpStatus.UNAUTHORIZED).setData(responseDto);
        }
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Gets a new access token using a refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        ApiResponse<AuthResponse> apiResponse = new ApiResponse<>();
        
        if (request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
            throw new IllegalArgumentException("Refresh token is required");
        }
        
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
            .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));
            
        refreshToken = refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();
        String token = authService.generateToken(user);
        
        AuthResponse authResponse = new AuthResponse(user.getId(), token, request.getRefreshToken());
        apiResponse.setData(authResponse).setStatus(HttpStatus.OK);
        
        return ApiResponse.getResponseEntity(apiResponse);
    }
}