package org.ecommerce.commons.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }

    public UnauthorizedException(String message, String code) {
        super(message, HttpStatus.UNAUTHORIZED, code);
    }
} 