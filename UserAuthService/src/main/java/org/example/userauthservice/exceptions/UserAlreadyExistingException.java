package org.example.userauthservice.exceptions;

public class UserAlreadyExistingException extends RuntimeException {
    public UserAlreadyExistingException(String message) {
        super(message);
    }
}