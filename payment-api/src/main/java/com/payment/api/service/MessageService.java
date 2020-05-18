package com.payment.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

/**
 * The type Message service.
 */
@Service
public class MessageService {

	private final MessageSource messageSource;

	/**
	 * Instantiates a new Message service.
	 *
	 * @param messageSource the message source
	 */
	@Autowired
	public MessageService(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * Gets message.
	 *
	 * @param code the code
	 * @return the message
	 */
	public String getMessage(String code) {
		return this.getMessage(code, null);
	}

	/**
	 * Gets message.
	 *
	 * @param code the code
	 * @param args the args
	 * @return the message
	 */
	public String getMessage(String code, Object[] args) {
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}
}