package com.reliaquest.api.exception;
/**
 * Exception thrown when a resource is not found.
 */
public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(message);
    }
}
