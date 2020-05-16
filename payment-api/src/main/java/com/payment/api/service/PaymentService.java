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
import com.payment.api.model.criteria.CardFullTextCriteria;
import com.payment.api.model.criteria.SearchCriteria;
import com.payment.api.model.entity.CancellationEntity;
import com.payment.api.model.entity.PaymentEntity;
import com.payment.api.model.tuple.ApproveTuple;
import com.payment.api.model.tuple.CancelTuple;
import com.payment.api.model.tuple.CardTuple;
import com.payment.api.model.tuple.SearchTuple;
import com.payment.api.model.type.DelimiterType;
import com.payment.api.model.type.MessageType;
import com.payment.api.model.type.TimeoutType;
import com.payment.api.repository.GenerateSequenceRepository;
import com.payment.api.repository.mongo.PaymentMongoRepository;
import com.payment.api.repository.redis.PaymentRedisRepository;
import com.payment.api.util.CardApproveFullText;
import com.payment.api.util.CardFullText;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class PaymentService {
	
	private final PaymentRedisRepository paymentRedisRepository;
	private final PaymentMongoRepository paymentMongoRepository;
	private final GenerateSequenceRepository generateSequenceRepository;
	private final MessageService messageService;
	private final EncryptorComponent encryptorComponent;
	
	@Value("${payment-secretkey}")
	private String secretKey;

	@Autowired
	public PaymentService(PaymentRedisRepository paymentRedisRepository,
							PaymentMongoRepository paymentMongoRepository, 
							GenerateSequenceRepository generateSequenceRepository, 
							MessageService messageService,
							EncryptorComponent encryptorComponent) {
		this.paymentRedisRepository = paymentRedisRepository;
		this.paymentMongoRepository = paymentMongoRepository;
		this.generateSequenceRepository = generateSequenceRepository;
		this.messageService = messageService;
		this.encryptorComponent = encryptorComponent;
	}
	
	
	public SearchTuple search(final SearchCriteria searchCriteria) {
		Optional<PaymentEntity> documents = paymentMongoRepository.findById(searchCriteria.getMid());
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
						.cardInfo(this.getCardTuple(paymentEntity.getEncryptedCardInfo())) // 복호화
						.totalCanceledAmount(totalCanceledAmount)
						.totalCanceledVat(totalCanceledVat)
						.build();
	}
	
	public ApproveTuple approve(final ApproveCriteria approveCriteria) {
		String cardNumber = approveCriteria.getCardNumber();
		String mmyy = approveCriteria.getMmyy();
		String cvc = approveCriteria.getCvc();
		BigDecimal amount = approveCriteria.getAmount();
		BigDecimal vat = approveCriteria.getVat();
		
		// redis key set
		Optional.of(paymentRedisRepository.set(cardNumber, "1", TimeoutType.REDIS_APPROVE.getMilliseconds()))
			.filter(s -> s == Boolean.TRUE)
			.orElseThrow(() -> new PaymentApiException(HttpStatus.BAD_REQUEST, messageService.getMessage(MessageType.PAYMENT_ERROR_NODATA.getCode())));
		
		// 부가가치세 계산
		if (vat != null && vat.compareTo(BigDecimal.ZERO) > 0) {
			vat = amount.divide(BigDecimal.valueOf(11), 0, BigDecimal.ROUND_HALF_EVEN);
		}
		
		// mid 생성
		String mid = generateSequenceRepository.generateSequence(PaymentEntity.SEQUENCE_NAME);
		
		// 카드정보 암호화
		String encryptedCardInfo = this.getEncryptCardInfo(cardNumber, mmyy, cvc);
		
		// 카드사 전문생성
		CardFullText cardFullText = new CardApproveFullText(CardFullTextCriteria.builder()
																			.mid(mid)
																			.cardNumber(cardNumber)
																			.installMonth(approveCriteria.getInstallMonth())
																			.mmyy(mmyy)
																			.cvc(cvc)
																			.amount(approveCriteria.getAmount())
																			.vat(approveCriteria.getVat())
																			.encryptedCardInfo(encryptedCardInfo)
																			.build());
		String fullText = cardFullText.createFullText();
		
		// DB insert
		PaymentEntity entity = paymentMongoRepository.save(PaymentEntity.builder()
																.mid(mid)		
																.amount(approveCriteria.getAmount())
																.vat(approveCriteria.getVat())
																.fullText(fullText)
																.cancelAvailableAmount(approveCriteria.getAmount())
																.cancelAvailableVat(approveCriteria.getVat())
																.encryptedCardInfo(encryptedCardInfo)
																.approvedAt(LocalDateTime.now())
																.build());
		
		if ( log.isDebugEnabled() ) {
			log.debug("##################### 결제요청 결과 START #####################");
			log.debug("관리번호 : " + mid);
			log.debug("암호화 구문 : " + encryptedCardInfo);
			log.debug("카드사 전문통신 : " + fullText);
			log.debug("PaymentEntity : " + entity);
			log.debug("##################### 결제요청 결과 END #####################");
		}
		
		// redis key delete
		paymentRedisRepository.delete(cardNumber);
		
		return ApproveTuple.builder()
					.mid(entity.getMid())
					.amount(entity.getAmount())
					.vat(entity.getVat())
					.approvedAt(entity.getApprovedAt())
					.build();
	}
	
	public CancelTuple cancel(final CancelCriteria cancelCriteria) {
		String mid = cancelCriteria.getMid();
		BigDecimal cancelAmount = cancelCriteria.getCancelAmount();
		BigDecimal cancelVat = cancelCriteria.getCancelVat();
		
		// redis key set
		Optional.of(paymentRedisRepository.set(mid, "1", TimeoutType.REDIS_CANCEL.getMilliseconds()))
			.filter(s -> s == Boolean.TRUE)
			.orElseThrow(() -> new PaymentApiException(HttpStatus.BAD_REQUEST, messageService.getMessage(MessageType.PAYMENT_ERROR_NODATA.getCode())));
		
		// TODO
		// search
		// 부가가치세 관련 validation
		// mid 생성
		// 암호화
		// 카드사 전문생성
		// update
		// 결과생성
		
		// redis key delete
		paymentRedisRepository.delete(mid);
				
		return null;
	}
	
	private String getEncryptCardInfo(final String cardNumber, final String mmyy, final String cvc) {
		// validation
		if (StringUtils.isAllBlank(cardNumber, mmyy, cvc)) {
			throw new PaymentApiException(HttpStatus.BAD_REQUEST, messageService.getMessage(MessageType.PAYMENT_ERROR_CARDINFO.getCode()));
		}
		
		String planCardInfo = new StringBuffer().append(cardNumber)
				.append(DelimiterType.CARD.getDelimiter())
				.append(mmyy)
				.append(DelimiterType.CARD.getDelimiter())
				.append(cvc)
				.toString();

		// 암호화
		return encryptorComponent.encrypt(planCardInfo, secretKey);
	}
	
	private CardTuple getCardTuple(String encryptedCardInfo) {
		// validation
		if (StringUtils.isBlank(encryptedCardInfo)) {
			throw new PaymentApiException(HttpStatus.BAD_REQUEST, messageService.getMessage(MessageType.PAYMENT_ERROR_CARDINFO.getCode()));
		}
		
		// 복호화
		String[] splitedCardInfo = StringUtils.split(encryptorComponent.decrypt(encryptedCardInfo, secretKey), DelimiterType.CARD.getDelimiter());
		
		if ( CollectionUtils.sizeIsEmpty(splitedCardInfo) ) {
			throw new PaymentApiException(HttpStatus.BAD_REQUEST, messageService.getMessage(MessageType.PAYMENT_ERROR_CARDINFO.getCode()));
		}
		
		return CardTuple.builder()
						.cardNumber(splitedCardInfo[0])
						.mmyy(splitedCardInfo[1])
						.cvc(splitedCardInfo[2])
						.build();
	}
	
//	public static void main(String[] args) {
//		CardFullText cardFullText = new CardApproveFullText(CardFullTextCriteria.builder()
//				.mid("XXXXXXXXXXXXXXXXXXXX")
//				.cardNumber("1234567890123456")
//				.installMonth("0")
//				.mmyy("1125")
//				.cvc("777")
//				.amount(new BigDecimal("110000"))
//				.vat(new BigDecimal("10000"))
//				.encryptedCardInfo("YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY")
//				.build());
//		String fullText = cardFullText.createFullText();
//		
//		System.out.println("result : " + fullText);
//	}
	
//	public static void main(String[] args) {
//		BigDecimal amount = BigDecimal.valueOf(3300);
//		BigDecimal vat = amount.divide(BigDecimal.valueOf(11), 0, BigDecimal.ROUND_HALF_EVEN);
//		
//		System.out.println(vat);
//	}
	
}
