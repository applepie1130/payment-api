package com.payment.api.model.type.fulltext;

import org.apache.commons.lang3.StringUtils;

public enum FillOperationType {
	
    DIGIT,
    DIGIT_ZERO,
    DIGIT_L,
    STRING
    ;

    public String operate(String target, Integer size) {
    	target = (target != null) ? target : "";
    	
    	switch (this) {
            case DIGIT:
            	return StringUtils.leftPad(target, size, FillType.SPACE.getText());
            	
            case DIGIT_ZERO:
            	return StringUtils.leftPad(target, size, FillType.ZERO.getText());
            	
            case DIGIT_L:
            	return StringUtils.rightPad(target, size, FillType.SPACE.getText());
            	
            case STRING:
            	return StringUtils.rightPad(target, size, FillType.SPACE.getText());
        }
    	
    	throw new AssertionError("Unknown operation" + this);
    }
}