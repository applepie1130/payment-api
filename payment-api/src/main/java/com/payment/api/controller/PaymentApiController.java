package com.payment.api.controller;

import com.payment.api.model.criteria.ApproveCriteria;
import com.payment.api.model.criteria.CancelCriteria;
import com.payment.api.model.criteria.SearchCriteria;
import com.payment.api.model.entity.PaymentResponseEntity;
import com.payment.api.model.tuple.ApproveTuple;
import com.payment.api.model.tuple.CancelTuple;
import com.payment.api.model.tuple.SearchTuple;
import com.payment.api.model.type.MessageType;
import com.payment.api.service.MessageService;
import com.payment.api.service.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * The type Payment api controller.
 */
@Log4j2
@RestController
@RequestMapping(path = "/v1/payment/", produces = "application/json")
@Api(tags = "PaymentApiController", value = "Payment API", produces = "application/json")
public class PaymentApiController {
	
	private final PaymentService paymentService;
	private final MessageService messageService;

	/**
	 * Instantiates a new Payment api controller.
	 *
	 * @param paymentService the payment service
	 * @param messageService the message service
	 */
	@Autowired
	public PaymentApiController(PaymentService paymentService, MessageService messageService) {
		this.paymentService = paymentService;
		this.messageService = messageService;
	}

	/**
	 * Search response entity.
	 *
	 * @param searchCriteria the search criteria
	 * @return the response entity
	 */
	@GetMapping(path="search/{mid}")
	@ApiOperation(
			httpMethod = "GET",
			value = "TODO 리스트 조회 API",
			notes = "키워드로 TODO 일정 리스트 정보를 검색한다.",
			response = PaymentResponseEntity.class
			)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "mid", value = "관리번호", required = true, dataType = "string", paramType = "path", example = "20200518000000000000")
	})
	public ResponseEntity<PaymentResponseEntity> search(@Valid @PathVariable("mid") final SearchCriteria searchCriteria) {
		
		SearchTuple result = paymentService.search(searchCriteria);
		
		return new ResponseEntity<>(PaymentResponseEntity.builder()
									.result(result)
									.status(HttpStatus.OK)
									.message(messageService.getMessage(MessageType.PAYMENT_SUCCESS_SEARCH.getCode(), null))
									.build()
									, HttpStatus.OK);
	}

	/**
	 * Approve response entity.
	 *
	 * @param approveCriteria the approve criteria
	 * @return the response entity
	 */
	@PostMapping(path="approve")
	@ApiOperation(
			httpMethod = "POST",
			value = "결제요청 API",
			notes = "카드사 승인 전문통신 수행 및 결제처리 한다.",
			response = PaymentResponseEntity.class
			)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "cardNumber", value = "카드번호 (10~16자리숫자)", required = true, dataType = "string", paramType = "query", example = ""),
		@ApiImplicitParam(name = "mmyy", value = "카드유효기간(mmyy) (4자리)", required = true, dataType = "string", paramType = "query", example = ""),
		@ApiImplicitParam(name = "cvc", value = "카드CVC (3자리)", required = true, dataType = "string", paramType = "query", example = ""),
		@ApiImplicitParam(name = "installMonth", value = "할부개월 0~12 (0:일시불, 1~12:개월수)", required = true, dataType = "string", paramType = "query", example = ""),
		@ApiImplicitParam(name = "amount", value = "결제금액 (100원이상 , 10억이하)", required = true, dataType = "int", paramType = "query", example = ""),
		@ApiImplicitParam(name = "vat", value = "부가가치세", required = false, dataType = "int", paramType = "query", example = "")
	})
	public ResponseEntity<PaymentResponseEntity> approve(@Valid @ModelAttribute final ApproveCriteria approveCriteria) {
		
		ApproveTuple result = paymentService.approve(approveCriteria);
		
		return new ResponseEntity<>(PaymentResponseEntity.builder()
				.status(HttpStatus.OK)
				.result(result)
				.message(messageService.getMessage(MessageType.PAYMENT_SUCCESS_APPROVE.getCode(), null))
				.build()
				, HttpStatus.OK);
	}

	/**
	 * Cancel response entity.
	 *
	 * @param cancelCriteria the cancel criteria
	 * @return the response entity
	 */
	@PostMapping(path="cancel")
	@ApiOperation(
			httpMethod = "POST",
			value = "결제취소 API",
			notes = "카드사 승인취소 전문통신 수행 및 결제취소 한다.",
			response = PaymentResponseEntity.class
			)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "mid", value = "관리번호", required = true, dataType = "string", paramType = "query", example = "20200518000000000000"),
		@ApiImplicitParam(name = "cancelAmount", value = "취소/부분취소 요청 금액", required = true, dataType = "int", paramType = "query", example = ""),
		@ApiImplicitParam(name = "cancelVat", value = "취소 부가가치세", required = false, dataType = "int", paramType = "query", example = "")
	})
	public ResponseEntity<PaymentResponseEntity> cancel(@Valid @ModelAttribute final CancelCriteria cancelCriteria) {
		
		CancelTuple result = paymentService.cancel(cancelCriteria);
		
		return new ResponseEntity<>(PaymentResponseEntity.builder()
				.status(HttpStatus.OK)
				.result(result)
				.message(messageService.getMessage(MessageType.PAYMENT_SUCCESS_CANCEL.getCode(), null))
				.build()
				, HttpStatus.OK);
	}
}