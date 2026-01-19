package com.ey.exception;

public class InvalidOrExpiredTokenException extends RuntimeException {
	public InvalidOrExpiredTokenException(String message) { super(message); }

}
