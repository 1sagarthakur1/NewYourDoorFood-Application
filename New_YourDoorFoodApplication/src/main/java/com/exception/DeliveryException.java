package com.exception;

@SuppressWarnings("serial")
public class DeliveryException extends RuntimeException {

	public DeliveryException() {

	}
	
	public DeliveryException(String msg) {

		super(msg);
	}
}
