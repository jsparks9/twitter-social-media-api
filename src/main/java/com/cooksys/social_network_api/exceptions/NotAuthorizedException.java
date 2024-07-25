package com.cooksys.social_network_api.exceptions;

public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException() { super(); }
    public NotAuthorizedException(Throwable cause) { super(cause); }
    public NotAuthorizedException(String message) { super(message); }
}
