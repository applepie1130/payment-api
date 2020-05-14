package com.payment.api.model.type;

public enum DelimiterType {
	
	CARD("|")
	;

	private String delimiter;
	
	private DelimiterType(final String delimiter) {
		this.delimiter = delimiter;
	}
	
	public String getDelimiter() {
		return this.delimiter;
	}
}
