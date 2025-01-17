package com.reliaquest.api.exception;

/**
 * Custom exception class for handling API-related errors.
 */
public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
