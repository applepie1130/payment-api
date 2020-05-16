package com.payment.api.model.type.fulltext;

import lombok.Getter;

@Getter
public enum FullTextBodyType {

	CARD_NO(20, FillOperationType.DIGIT_L),
	INSTALL_MONTH(2, FillOperationType.DIGIT_ZERO),
	MMYY(4, FillOperationType.DIGIT_L),
	CVC(3, FillOperationType.DIGIT_L),
	AMOUNT(10, FillOperationType.DIGIT),
	VAT(10, FillOperationType.DIGIT_ZERO),
	ORIGIN_MID(20, FillOperationType.STRING),
	ENCRYPTED_CARD_INFO(300, FillOperationType.STRING),
	ETC(47, FillOperationType.STRING),
	;

	private Integer size; // 자릿수(길이)
	private FillOperationType fillOperationType; // 오퍼레이션
	
	private FullTextBodyType(final Integer size, final FillOperationType fillOperationType) {
		this.size = size;
		this.fillOperationType = fillOperationType;
	}
	
}
