package com.example.Backend.Exception;

public class InvalidTableException extends RuntimeException {

    public InvalidTableException(String message) {
        super(message);
    }
}
