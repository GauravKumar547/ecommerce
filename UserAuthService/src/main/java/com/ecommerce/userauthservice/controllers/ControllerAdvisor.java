package com.ecommerce.userauthservice.controllers;


import com.ecommerce.commons.utils.response.ApiResponse;
import com.ecommerce.userauthservice.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler({IllegalArgumentException.class, UserAlreadyExistingException.class})
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(Exception e) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setError(e.getMessage()).setStatus(HttpStatus.BAD_REQUEST);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setError(e.getMessage()).setStatus(HttpStatus.NOT_ACCEPTABLE);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @ExceptionHandler(UserNotRegisteredException.class)
    public ResponseEntity<ApiResponse<String>> handleException(UserNotRegisteredException e) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setError(e.getMessage()).setStatus(HttpStatus.NOT_FOUND);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @ExceptionHandler({InvalidCredentialsException.class, UserUnauthorizedException.class, NoActiveSessionFoundException.class})
    public ResponseEntity<ApiResponse<String>> handleException404(Exception e) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setError(e.getMessage()).setStatus(HttpStatus.UNAUTHORIZED);
        return ApiResponse.getResponseEntity(apiResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setError(e.getMessage()).setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return ApiResponse.getResponseEntity(apiResponse);
    }
}