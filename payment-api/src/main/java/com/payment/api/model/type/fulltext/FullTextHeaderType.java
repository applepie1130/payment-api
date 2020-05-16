package com.payment.api.model.type.fulltext;

import lombok.Getter;

@Getter
public enum FullTextHeaderType {
	
	FULL_TEXT_LENGTH(4, FillOperationType.DIGIT),
	PAYMENT_TYPE(10, FillOperationType.STRING),
	MID(20, FillOperationType.STRING),
	;

	private Integer size; // 길이
	private FillOperationType fillOperationType; // 오퍼레이션
	
	private FullTextHeaderType(final Integer size, final FillOperationType fillOperationType) {
		this.size = size;
		this.fillOperationType = fillOperationType;
	}
}
