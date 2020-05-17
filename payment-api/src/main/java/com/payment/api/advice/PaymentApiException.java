package com.payment.api.advice;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class PaymentApiException extends RuntimeException {

	private static final long serialVersionUID = 4196785693889051006L;
	
	private HttpStatus status;
	private String message;
	private String key;

	public PaymentApiException(HttpStatus status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
	
	public PaymentApiException(HttpStatus status, String message, String key) {
		super();
		this.status = status;
		this.message = message;
		this.key = key;
	}
	
}