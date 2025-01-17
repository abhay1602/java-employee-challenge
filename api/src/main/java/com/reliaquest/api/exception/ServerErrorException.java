package com.reliaquest.api.exception;

/**
 * Exception thrown when the API responds with a server error.
 */
public class ServerErrorException extends ApiException {
    public ServerErrorException(String message) {
        super(message);
    }
}
