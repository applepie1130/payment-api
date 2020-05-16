//
//package com.payment.api.model.criteria.validator;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.validation.Errors;
//import org.springframework.validation.Validator;
//
//import com.payment.api.model.criteria.SearchCriteria;
//
//import lombok.extern.log4j.Log4j2;
//
//@Log4j2
//public class SearchCriteriaValidator implements Validator {
//	
//	@Override
//	public boolean supports(Class<?> clazz) {
//		return SearchCriteria.class.isAssignableFrom(clazz);
//	}
//
//	@Override
//	public void validate(Object target, Errors errors) {
//		
//		if (target == null) {
//			errors.reject("");
//			return;
//		}
//		
//		SearchCriteria searchCriteria = (SearchCriteria) target;
//		
//		if ( StringUtils.isBlank(searchCriteria.getMid()) ) {
////			errors.rejectValue("itemCode", PlanMessageType.VALIDATION_REQUIRED_FIELD.getCode(), new String[] { "상품코드" }, null);
//			errors.rejectValue("mid", "관리번호는 필수입니다.");
//		}
//	}
//
//}
