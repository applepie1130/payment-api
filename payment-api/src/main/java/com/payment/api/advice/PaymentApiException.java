package com.payment.api.advice;

import org.springframework.http.HttpStatus;

public class PaymentApiException extends RuntimeException {

	private static final long serialVersionUID = 4196785693889051006L;
	
	private HttpStatus status;
	private String message;

	public PaymentApiException(HttpStatus status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}