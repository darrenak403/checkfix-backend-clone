package com.datnguyen.instrumentservice.exception;
//hello
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}