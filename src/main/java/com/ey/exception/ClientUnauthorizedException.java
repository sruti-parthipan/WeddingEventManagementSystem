package com.ey.exception;


public class ClientUnauthorizedException extends RuntimeException {
    public ClientUnauthorizedException(String message) {
        super(message);
    }
}

