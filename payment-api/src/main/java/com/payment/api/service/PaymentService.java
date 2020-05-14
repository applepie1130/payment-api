package com.payment.api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.api.model.criteria.ApproveCriteria;
import com.payment.api.model.criteria.CancelCriteria;
import com.payment.api.model.tuple.ApproveTuple;
import com.payment.api.model.tuple.CancelTuple;
import com.payment.api.model.tuple.SearchTuple;
import com.payment.api.repository.mongo.PaymentMongoRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class PaymentService {
	
	private final PaymentMongoRepository paymentMongoRepository;
	private final GenerateSequenceService generateSequenceService;
	private final MessageService messageService;

	@Autowired
	public PaymentService(PaymentMongoRepository paymentMongoRepository, 
							GenerateSequenceService generateSequenceService, 
							MessageService messageService) {
		this.paymentMongoRepository = paymentMongoRepository;
		this.generateSequenceService = generateSequenceService;
		this.messageService = messageService;
	}
	
	public SearchTuple search(final Optional<String> mid) {
		
		// validation
		String id = mid.orElseGet(() -> "not provided");
		
		return null;
	}
	
	public ApproveTuple approve(final ApproveCriteria approveCriteria) {
		
		// validation
		
		return null;
	}
	
	public CancelTuple cancel(final CancelCriteria cancelCriteria) {

		// validation
		
		return null;
	}
}
