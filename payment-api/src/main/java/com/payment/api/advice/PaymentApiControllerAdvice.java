package com.payment.api.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.payment.api.model.entity.PaymentResponseEntity;

@RestControllerAdvice
public class PaymentApiControllerAdvice {

	@ExceptionHandler(PaymentApiException.class)
	public ResponseEntity<PaymentResponseEntity> handler(PaymentApiException e) {
		
		PaymentResponseEntity response = PaymentResponseEntity.builder()
			.message(e.getMessage())
			.status(e.getStatus())
			.build();
		
		return new ResponseEntity<>(response, e.getStatus());
	}
}
