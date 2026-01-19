package com.ey.exception;

public class PaymentAlreadyProcessedException extends RuntimeException {
	public PaymentAlreadyProcessedException(String message) { super(message); }
}
