package com.ecommerce.userauthservice.exceptions;

public class NoActiveSessionFoundException extends RuntimeException {
    public NoActiveSessionFoundException(String message) {
        super(message);
    }
}