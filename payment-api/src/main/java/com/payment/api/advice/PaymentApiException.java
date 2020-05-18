package com.payment.api.advice;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * The type Payment api exception.
 */
@Data
public class PaymentApiException extends RuntimeException {

	private static final long serialVersionUID = 4196785693889051006L;
	
	private HttpStatus status;
	private String message;
	private String key;

	/**
	 * Instantiates a new Payment api exception.
	 *
	 * @param status  the status
	 * @param message the message
	 */
	public PaymentApiException(HttpStatus status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

	/**
	 * Instantiates a new Payment api exception.
	 *
	 * @param status  the status
	 * @param message the message
	 * @param key     the key
	 */
	public PaymentApiException(HttpStatus status, String message, String key) {
		super();
		this.status = status;
		this.message = message;
		this.key = key;
	}
	
}