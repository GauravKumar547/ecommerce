package org.example.userauthservice.controllers.impl;

import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthservice.controllers.AuthController;
import org.example.userauthservice.dtos.LoginRequestDto;
import org.example.userauthservice.dtos.ResponseDto;
import org.example.userauthservice.dtos.UserDto;
import org.example.userauthservice.dtos.ValidateTokenDto;
import org.example.userauthservice.mappers.UserMapper;
import org.example.userauthservice.models.User;
import org.example.userauthservice.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.example.userauthservice.utils.ApiResponse;

@RestController
@RequestMapping("/auth")
public class AuthControllerImpl implements AuthController {
    private final AuthService authService;

    @Autowired
    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Override
    public ResponseEntity<ApiResponse<UserDto>> login(@RequestBody LoginRequestDto requestDto) {
        ApiResponse<UserDto> apiResponse = new ApiResponse<>();
        if(requestDto.getEmail() == null || requestDto.getPassword() == null || requestDto.getEmail().isEmpty() || requestDto.getPassword().isEmpty()) {
           apiResponse.setError("No email or password provided").setStatus(HttpStatus.BAD_REQUEST);
           return ApiResponse.getResponseEntity(apiResponse);
        }
        Pair<User, String> loggedInDetails = authService.login(requestDto.getEmail(), requestDto.getPassword());
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        ResponseCookie cookie = ResponseCookie.from("token", loggedInDetails.b).build();
        headers.add(HttpHeaders.SET_COOKIE,cookie.toString());
        apiResponse.setStatus(HttpStatus.OK).setData(UserMapper.toUserDto(loggedInDetails.a));
        return ApiResponse.getResponseEntity(apiResponse, headers);
    }
    @PostMapping("/signup")
    @Override
    public ResponseEntity<ApiResponse<ResponseDto>> signup(@RequestBody UserDto requestDto) {
        if(requestDto.getEmail() == null || requestDto.getPassword() == null || requestDto.getName() == null|| requestDto.getEmail().isEmpty() || requestDto.getPassword().isEmpty()||requestDto.getName().isEmpty()) {
             throw new IllegalArgumentException("Required parameters not provided");
        }
        ApiResponse<ResponseDto> apiResponse = new ApiResponse<>();
        if(authService.signup(UserMapper.toUser(requestDto))){
            ResponseDto responseDto = new ResponseDto();
            responseDto.setMessage("User registered successfully");
            apiResponse.setStatus(HttpStatus.CREATED).setData(responseDto);
        }
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/logout/{id}")
    @Override
    public ResponseEntity<ApiResponse<ResponseDto>> logout(@RequestHeader(name = "Set-Cookie", required = false) String cookie, @PathVariable long id) {
        ApiResponse<ResponseDto> apiResponse = new ApiResponse<>();
        authService.logout(cookie,id);
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("User logged out successfully");
        apiResponse.setStatus(HttpStatus.OK).setData(responseDto);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/validate_token")
    @Override
    public ResponseEntity<ApiResponse<ResponseDto>> validateToken(@RequestBody ValidateTokenDto requestDto) {
        if (requestDto.getToken() == null || requestDto.getToken().isEmpty() ) {
            throw new IllegalArgumentException("Token not provided");
        }
        if (requestDto.getUserId() <=0){
            throw new IllegalArgumentException("User id not provided");
        }
        ApiResponse<ResponseDto> apiResponse = new ApiResponse<>();
        ResponseDto responseDto = new ResponseDto();
        if( authService.validateToken(requestDto.getToken(),requestDto.getUserId())){
            responseDto.setMessage("Token validated successfully");
            apiResponse.setStatus(HttpStatus.OK).setData(responseDto);
        }else{
            responseDto.setMessage("Invalid token");
            apiResponse.setStatus(HttpStatus.UNAUTHORIZED).setData(responseDto);
        }
        return ApiResponse.getResponseEntity(apiResponse);
    }
}