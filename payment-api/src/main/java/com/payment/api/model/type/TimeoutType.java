package com.payment.api.model.type;

public enum TimeoutType {
	
	REDIS_APPROVE(2000),
	REDIS_CANCEL(2000)
	;

	private Integer milliseconds;
	
	private TimeoutType(final Integer milliseconds) {
		this.milliseconds = milliseconds;
	}
	
	public Integer getMilliseconds() {
		return this.milliseconds;
	}
	
}
