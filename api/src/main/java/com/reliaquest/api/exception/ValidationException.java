package com.reliaquest.api.exception;

/**
 * Exception thrown when input validation fails.
 */
public class ValidationException extends ApiException {
    public ValidationException(String message) {
        super(message);
    }
}
