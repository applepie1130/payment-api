package com.payment.api.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.payment.api.advice.PaymentApiException;
import com.payment.api.component.EncryptorComponent;
import com.payment.api.model.criteria.ApproveCriteria;
import com.payment.api.model.criteria.CancelCriteria;
import com.payment.api.model.entity.CancellationEntity;
import com.payment.api.model.entity.PaymentEntity;
import com.payment.api.model.tuple.ApproveTuple;
import com.payment.api.model.tuple.CancelTuple;
import com.payment.api.model.tuple.CardTuple;
import com.payment.api.model.tuple.SearchTuple;
import com.payment.api.model.type.DelimiterType;
import com.payment.api.model.type.MessageType;
import com.payment.api.repository.mongo.PaymentMongoRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class PaymentService {
	
	private final PaymentMongoRepository paymentMongoRepository;
	private final GenerateSequenceService generateSequenceService;
	private final MessageService messageService;
	private final EncryptorComponent encryptorComponent;
	
	@Value("${payment-secretkey}")
	private String secretKey;

	@Autowired
	public PaymentService(PaymentMongoRepository paymentMongoRepository, 
							GenerateSequenceService generateSequenceService, 
							MessageService messageService,
							EncryptorComponent encryptorComponent) {
		this.paymentMongoRepository = paymentMongoRepository;
		this.generateSequenceService = generateSequenceService;
		this.messageService = messageService;
		this.encryptorComponent = encryptorComponent;
	}
	
	public SearchTuple search(final Optional<String> mid) {
		
		// validation
		String id = mid.orElseGet(() -> "not provided");
		
		Optional<PaymentEntity> documents = paymentMongoRepository.findById(id);
		documents.filter(Objects::nonNull).orElseThrow(() -> new PaymentApiException(HttpStatus.BAD_REQUEST, messageService.getMessage(MessageType.PAYMENT_ERROR_NODATA.getCode())));
		PaymentEntity paymentEntity = documents.get();
		List<CancellationEntity> canceledList = paymentEntity.getCanceledList();
		
		BigDecimal totalCanceledAmount = BigDecimal.ZERO;
		BigDecimal totalCanceledVat = BigDecimal.ZERO;
		
		if ( CollectionUtils.isNotEmpty(canceledList) ) {
			totalCanceledAmount = canceledList.stream()
											.map(s->s.getCancelAmount())
											.reduce(BigDecimal.ZERO, BigDecimal::add);
			
			totalCanceledVat = canceledList.stream()
						.map(s->s.getCancelVat())
						.reduce(BigDecimal.ZERO, BigDecimal::add);
		}			 
		
		return SearchTuple.builder()
						.mid(paymentEntity.getMid())
						.amount(paymentEntity.getAmount())
						.vat(paymentEntity.getVat())
						.cancelAvailableAmount(paymentEntity.getCancelAvailableAmount())
						.approvedAt(paymentEntity.getApprovedAt())
						.cardInfo(this.getCardTuple(paymentEntity.getEncryptedCardInfo()))
						.totalCanceledAmount(totalCanceledAmount)
						.totalCanceledVat(totalCanceledVat)
						.build();
		
	}
	
	public ApproveTuple approve(final ApproveCriteria approveCriteria) {
		
		// validation
		String cardNumber = approveCriteria.getCardNumber();
		String mmyy = approveCriteria.getMmyy();
		String cvc = approveCriteria.getCvc();
		
		if (StringUtils.isAllBlank(cardNumber, mmyy, cvc)) {
			
		}
		
		/** redis set **/
		
		// 카드전문통신
		
		PaymentEntity entity = paymentMongoRepository.save(PaymentEntity.builder()
																.mid(generateSequenceService.generateSequence(PaymentEntity.SEQUENCE_NAME))		
																.amount(approveCriteria.getAmount())
																.vat(approveCriteria.getVat())
																.cancelAvailableAmount(approveCriteria.getAmount())
																.cancelAvailableVat(approveCriteria.getVat())
																.encryptedCardInfo(this.getEncryptCardInfo(cardNumber, mmyy, cvc))
																.approvedAt(LocalDateTime.now())
																.build());
		/** redis release **/ 
		
		return ApproveTuple.builder()
					.mid(entity.getMid())
					.amount(entity.getAmount())
					.vat(entity.getVat())
					.approvedAt(entity.getApprovedAt())
					.build();
	}
	
	public CancelTuple cancel(final CancelCriteria cancelCriteria) {

		// validation
		
		return null;
	}
	
	private CardTuple getCardTuple(String encryptedCardInfo) {
		
		if (StringUtils.isBlank(encryptedCardInfo)) {
			// TODO
		}
		
		String[] splitedCardInfo = StringUtils.split(encryptorComponent.decrypt(encryptedCardInfo, secretKey),
											DelimiterType.CARD.getDelimiter());
		
		if ( CollectionUtils.sizeIsEmpty(splitedCardInfo) ) {
			// TODO
		}
		
		return CardTuple.builder()
						.cardNumber(splitedCardInfo[0])
						.mmyy(splitedCardInfo[1])
						.cvc(splitedCardInfo[2])
						.build();
	}
	
	private String getEncryptCardInfo(String cardNumber, String mmyy, String cvc) {
		
		if (StringUtils.isAllBlank(cardNumber, mmyy, cvc)) {
			// TODO 
		}
		
		String planCardInfo = new StringBuffer().append(cardNumber)
				.append(DelimiterType.CARD.getDelimiter())
				.append(mmyy)
				.append(DelimiterType.CARD.getDelimiter())
				.append(cvc)
				.toString();

		return encryptorComponent.encrypt(planCardInfo, secretKey);
		
	}
}
