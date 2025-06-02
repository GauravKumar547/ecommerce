package org.ecommerce.emailservice.exceptions;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.ecommerce.emailservice.utils.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
        );
        apiResponse.setError(errors.toString()).setStatus(HttpStatus.BAD_REQUEST);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleConstraintViolation(ConstraintViolationException ex) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        StringBuilder errors = new StringBuilder();
        ex.getConstraintViolations().forEach(violation -> 
            errors.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ")
        );
        apiResponse.setError(errors.toString()).setStatus(HttpStatus.BAD_REQUEST);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleEntityNotFoundException(EntityNotFoundException e) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setError(e.getMessage()).setStatus(HttpStatus.NOT_FOUND);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setError(e.getMessage()).setStatus(HttpStatus.NOT_ACCEPTABLE);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setError(e.getMessage()).setStatus(HttpStatus.BAD_REQUEST);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setError(e.getMessage()).setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return ApiResponse.getResponseEntity(apiResponse);
    }
} 