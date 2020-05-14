package com.payment.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

	private final MessageSource messageSource;
	
	@Autowired
	public MessageService(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String getMessage(String code) {
		return this.getMessage(code, null);
	}
	
	public String getMessage(String code, Object[] args) {
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}
}