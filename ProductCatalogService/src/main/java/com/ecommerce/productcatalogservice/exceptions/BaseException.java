package com.ecommerce.productcatalogservice.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public BaseException(String message, HttpStatus status, String code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public BaseException(String message, HttpStatus status) {
        this(message, status, null);
    }

    public BaseException(String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 