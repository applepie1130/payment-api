package com.payment.api.model.type;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="StatusType", description="결제상태 타입")
public enum StatusType {
	
	@ApiModelProperty(notes = "결제완료", name = "SUCCESS_PAYMENT")
	SUCCESS_PAYMENT,
	
	@ApiModelProperty(notes = "부분취소", name = "PART_CANCEL_PAYMENT")
	PART_CANCEL_PAYMENT,
	
	@ApiModelProperty(notes = "결제된금액 모두 취소", name = "CANCEL_PAYMENT")
	CANCEL_PAYMENT
	
}
