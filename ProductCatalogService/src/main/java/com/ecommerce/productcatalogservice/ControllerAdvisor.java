package com.ecommerce.productcatalogservice;

import com.ecommerce.productcatalogservice.utils.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvisor {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException e) {
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setError(e.getMessage()).setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return ApiResponse.getResponseEntity(apiResponse);
    }
}