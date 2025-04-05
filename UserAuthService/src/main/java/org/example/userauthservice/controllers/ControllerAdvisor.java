package org.example.userauthservice.controllers;

import org.example.userauthservice.dtos.ResponseDto;
import org.example.userauthservice.utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ResponseDto>> handleException(Exception ex, WebRequest request) {
        ApiResponse<ResponseDto> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(HttpStatus.BAD_REQUEST).setError(ex.getMessage());
        return ApiResponse.getResponseEntity(apiResponse);
    }
}
