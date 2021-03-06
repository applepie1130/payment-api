package com.payment.api.advice;

import com.payment.api.model.entity.PaymentResponseEntity;
import com.payment.api.repository.redis.PaymentRedisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * The type Payment advice.
 */
@RestControllerAdvice
public class PaymentAdvice {
	
	private final PaymentRedisRepository paymentRedisRepository;

	/**
	 * Instantiates a new Payment advice.
	 *
	 * @param paymentRedisRepository the payment redis repository
	 */
	public PaymentAdvice(PaymentRedisRepository paymentRedisRepository) {
		this.paymentRedisRepository = paymentRedisRepository;
	}

	/**
	 * Handler response entity.
	 *
	 * @param e the e
	 * @return the response entity
	 */
	@ExceptionHandler(PaymentApiException.class)
	protected ResponseEntity<PaymentResponseEntity> handler(PaymentApiException e) {
		if (StringUtils.isNoneBlank(e.getKey())) {
			// redis key delete
			paymentRedisRepository.delete(e.getKey());
		}
		
		PaymentResponseEntity response = PaymentResponseEntity.builder()
			.message(e.getMessage())
			.status(e.getStatus())
			.build();
		
		return new ResponseEntity<>(response, e.getStatus());
	}

	/**
	 * Handle bind exception response entity.
	 *
	 * @param e the e
	 * @return the response entity
	 */
	@ExceptionHandler(BindException.class)
    protected ResponseEntity<PaymentResponseEntity> handleBindException(BindException e) {
		FieldError fieldError = e.getBindingResult().getFieldError();
	    PaymentResponseEntity response = PaymentResponseEntity.builder()
				.message(fieldError.getDefaultMessage())
				.status(HttpStatus.BAD_REQUEST)
				.build();
	    
	    return new ResponseEntity<>(response, response.getStatus());
    }

	/**
	 * Handler response entity.
	 *
	 * @param e the e
	 * @return the response entity
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<PaymentResponseEntity> handler(MethodArgumentNotValidException e) {
		FieldError fieldError = e.getBindingResult().getFieldError();
	    PaymentResponseEntity response = PaymentResponseEntity.builder()
	    		.message(fieldError.getDefaultMessage())
				.status(HttpStatus.BAD_REQUEST)
				.build();

	    return new ResponseEntity<>(response, response.getStatus());
	}
}