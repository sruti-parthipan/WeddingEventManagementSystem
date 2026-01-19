package com.ey.exception;



public class BookingCreationException extends RuntimeException {
    public BookingCreationException(String message) {
        super(message);
    }
}