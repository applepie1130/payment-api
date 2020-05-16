package com.payment.api.model.type.fulltext;

import lombok.Getter;

@Getter
public enum FillType {
	
	SPACE("_"),
	ZERO("0")
	;

	private String text;
	
	private FillType(final String text) {
		this.text = text;
	}
	
}
