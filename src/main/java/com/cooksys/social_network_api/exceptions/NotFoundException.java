package com.cooksys.social_network_api.exceptions;

// HTTP response code 404
public class NotFoundException extends RuntimeException {
    public NotFoundException() { super(); }
    public NotFoundException(Throwable cause) { super(cause); }
    public NotFoundException(String message) { super(message); }
}
