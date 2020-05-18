package com.payment.api.model.type;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public enum MessageType {
	
	// SUCCESS
	PAYMENT_SUCCESS_SEARCH("payment.success.search"),
	PAYMENT_SUCCESS_APPROVE("payment.success.approve"),
	PAYMENT_SUCCESS_CANCEL("payment.success.cancel"),
	
	// ERROR
	PAYMENT_ERROR_DEFAULT("payment.error.default"),
	PAYMENT_ERROR_ALEADY_PROCESS("payment.error.aleadyProcess"),
	PAYMENT_ERROR_NODATA("payment.error.nodata"),
	PAYMENT_ERROR_SEARCH("payment.error.search"),
	PAYMENT_ERROR_APPROVE("payment.error.approve"),
	PAYMENT_ERROR_CANCEL("payment.error.cancel"),
	PAYMENT_ERROR_ALEADY_CANCELED("payment.error.aleadyCanceled"),
	PAYMENT_ERROR_OVER_CANCEL_AMOUNT("payment.error.overCancelAmount"),
	PAYMENT_ERROR_OVER_CANCEL_VAT("payment.error.overCancelVat"),
	PAYMENT_ERROR_REMAIN_CANCEL_VAT("payment.error.remainCancelVat"),
	PAYMENT_ERROR_CARDINFO("payment.error.cardinfo"),
	
	;
	
	private String code;
	
	private MessageType(final String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}
	
	public static MessageType getTypeByCode(final String code) {
		if ( StringUtils.isBlank(code) ) {
			return null;
		}
		
		return Arrays.stream(MessageType.values())
					 .filter(type -> StringUtils.equals(type.getCode(), code))
					 .findAny()
					 .orElse(null);
	} 
}



