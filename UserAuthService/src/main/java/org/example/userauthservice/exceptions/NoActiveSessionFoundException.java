package org.example.userauthservice.exceptions;

public class NoActiveSessionFoundException extends RuntimeException {
    public NoActiveSessionFoundException(String message) {
        super(message);
    }
}