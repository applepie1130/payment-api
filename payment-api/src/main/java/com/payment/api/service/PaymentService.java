package com.payment.api.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.payment.api.model.type.StatusType;
import com.payment.api.model.type.TimeoutType;
import com.payment.api.repository.GenerateSequenceRepository;
import com.payment.api.repository.mongo.PaymentMongoRepository;
import com.payment.api.repository.redis.PaymentRedisRepository;
import com.payment.api.util.CardApproveFullText;
import com.payment.api.util.CardCancelFullText;

import lombok.extern.log4j.Log4j2;

/**
 * The type Payment service.
 */
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

	/**
	 * Instantiates a new Payment service.
	 *
	 * @param paymentRedisRepository     the payment redis repository
	 * @param paymentMongoRepository     the payment mongo repository
	 * @param generateSequenceRepository the generate sequence repository
	 * @param messageService             the message service
	 * @param encryptorComponent         the encryptor component
	 */
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


	/**
	 * Search search tuple.
	 *
	 * @param searchCriteria the search criteria
	 * @return the search tuple
	 */
	public SearchTuple search(final SearchCriteria searchCriteria) {
		PaymentEntity paymentEntity = this.selectPaymentEntity(searchCriteria.getMid());
		List<CancellationEntity> canceledList = paymentEntity.getCanceledList();
		BigDecimal totalCanceledAmount = BigDecimal.ZERO;
		BigDecimal totalCanceledVat = BigDecimal.ZERO;
		
		// 전체 취소, 취소부과세 금액 계산
		if (CollectionUtils.isNotEmpty(canceledList)) {
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
						.cancelAvailableVat(paymentEntity.getCancelAvailableVat())
						.approvedAt(paymentEntity.getApprovedAt())
						.card(this.getCardTuple(paymentEntity.getEncryptedCardInfo()).convertResponseTuple()) // 복호화
						.totalCanceledAmount(totalCanceledAmount)
						.totalCanceledVat(totalCanceledVat)
						.statusType(paymentEntity.getStatusType())
						.build();
	}

	/**
	 * Approve approve tuple.
	 *
	 * @param approveCriteria the approve criteria
	 * @return the approve tuple
	 */
	public ApproveTuple approve(final ApproveCriteria approveCriteria) {
		String cardNumber = approveCriteria.getCardNumber();
		String mmyy = approveCriteria.getMmyy();
		String cvc = approveCriteria.getCvc();
		BigDecimal amount = approveCriteria.getAmount();
		BigDecimal vat = approveCriteria.getVat();
		String installMonth = approveCriteria.getInstallMonth();
		
		// redis key set
		Optional.of(paymentRedisRepository.set(cardNumber, "1", TimeoutType.REDIS_APPROVE.getMilliseconds()))
			.filter(s -> s == Boolean.TRUE)
			.orElseThrow(() -> new PaymentApiException(HttpStatus.BAD_REQUEST, messageService.getMessage(MessageType.PAYMENT_ERROR_ALEADY_PROCESS.getCode())));
		
		// 부가가치세 계산
		if (vat == null) {
			vat = amount.divide(BigDecimal.valueOf(11), 0, BigDecimal.ROUND_HALF_EVEN);
		}
		
		// mid 생성
		String mid = generateSequenceRepository.generateSequence(PaymentEntity.SEQUENCE_NAME);
		
		// 카드정보 암호화
		String encryptedCardInfo = this.getEncryptCardInfo(cardNumber, mmyy, cvc);
		
		// 카드사 전문생성
		String fullText = new CardApproveFullText(CardFullTextCriteria.builder()
														.mid(mid)
														.cardNumber(cardNumber)
														.installMonth(installMonth)
														.mmyy(mmyy)
														.cvc(cvc)
														.amount(amount)
														.vat(vat)
														.encryptedCardInfo(encryptedCardInfo)
														.build())
							.createFullText();
		// DB insert
		PaymentEntity entity = paymentMongoRepository.insert(PaymentEntity.builder()
																.mid(mid)		
																.amount(amount)
																.vat(vat)
																.installMonth(installMonth)
																.fullText(fullText)
																.statusType(StatusType.SUCCESS_PAYMENT)
																.cancelAvailableAmount(amount)
																.cancelAvailableVat(vat)
																.encryptedCardInfo(encryptedCardInfo)
																.approvedAt(LocalDateTime.now())
																.build());
		
		if (log.isDebugEnabled()) {
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

	/**
	 * Cancel cancel tuple.
	 *
	 * @param cancelCriteria the cancel criteria
	 * @return the cancel tuple
	 */
	public CancelTuple cancel(final CancelCriteria cancelCriteria) {
		String mid = cancelCriteria.getMid();
		BigDecimal cancelAmount = cancelCriteria.getCancelAmount();
		BigDecimal cancelVat = cancelCriteria.getCancelVat();
		
		// redis key set
		Optional.of(paymentRedisRepository.set(mid, "1", TimeoutType.REDIS_CANCEL.getMilliseconds()))
			.filter(s -> s == Boolean.TRUE)
			.orElseThrow(() -> new PaymentApiException(HttpStatus.BAD_REQUEST, messageService.getMessage(MessageType.PAYMENT_ERROR_ALEADY_PROCESS.getCode())));
		
		// 원 결제정보 조회
		PaymentEntity paymentEntity = this.selectPaymentEntity(mid);
		CardTuple cardTuple = this.getCardTuple(paymentEntity.getEncryptedCardInfo());
		
		/**
		 * 취소가능여부 유효성 검사
		 * 취소가능금액 >= 취소금액
		 * 취소가능부가세 >= 취소부가세
		 */
		BigDecimal cancelAvailableAmount = paymentEntity.getCancelAvailableAmount();
		BigDecimal cancelAvailableVat = paymentEntity.getCancelAvailableVat();
		
		// 부가가치세 계산
		if (cancelVat == null) {
			if (cancelAmount.compareTo(cancelAvailableAmount) == 0) {
				cancelVat = cancelAvailableVat;
			} else {
				cancelVat = cancelAmount.divide(BigDecimal.valueOf(11), 0, BigDecimal.ROUND_HALF_EVEN);
			}
		}
		
		/**
		 * 취소가능 유효성 검사
		 * 전체취소 완료건의 경우 취소불가
		 * 남은 취소가능금액보다 더 큰 금액을 취소 하려고 한 경우
		 * 전체취소상태가 되는 경우 남은 부가세보다 작은 부가세를 요청한 경우
		 * 남은 부가세보다 큰 금액의 부가세를 요청한 경우
		 */
		if (paymentEntity.getStatusType() == StatusType.CANCEL_PAYMENT) {
			throw new PaymentApiException(HttpStatus.BAD_REQUEST, messageService.getMessage(MessageType.PAYMENT_ERROR_ALEADY_CANCELED.getCode()), mid);
		}
		if (cancelAvailableAmount.compareTo(cancelAmount) < 0) {
			throw new PaymentApiException(HttpStatus.BAD_REQUEST, messageService.getMessage(MessageType.PAYMENT_ERROR_OVER_CANCEL_AMOUNT.getCode()), mid);
		} 
		if (cancelAvailableAmount.compareTo(cancelAmount) == 0 && cancelAvailableVat.compareTo(cancelVat) > 0) {
			throw new PaymentApiException(HttpStatus.BAD_REQUEST, messageService.getMessage(MessageType.PAYMENT_ERROR_REMAIN_CANCEL_VAT.getCode()), mid);
		}
		if (cancelAvailableVat.compareTo(cancelVat) < 0) {
			throw new PaymentApiException(HttpStatus.BAD_REQUEST, messageService.getMessage(MessageType.PAYMENT_ERROR_OVER_CANCEL_VAT.getCode()), mid);
		}
		
		BigDecimal cancelAvailableAmountForUpdate = cancelAvailableAmount.subtract(cancelAmount);
		BigDecimal cancelAvailableVatForUpdate = cancelAvailableVat.subtract(cancelVat);
		
		StatusType statusType = null;
		if ( cancelAvailableAmountForUpdate.compareTo(BigDecimal.ZERO) == 0 ) {
			statusType = StatusType.CANCEL_PAYMENT;
		} else {
			statusType = StatusType.PART_CANCEL_PAYMENT;
		}
		
		// 취소관리번호 생성
		String cid = generateSequenceRepository.generateSequence(CancellationEntity.SEQUENCE_NAME);
		
		// 카드사 취소전문 생성
		String cancelFullText = new CardCancelFullText(CardFullTextCriteria.builder()
										.mid(cid)
										.cardNumber(cardTuple.getCardNumber())
										.installMonth(paymentEntity.getInstallMonth())
										.mmyy(cardTuple.getMmyy())
										.cvc(cardTuple.getCvc())
										.amount(cancelAvailableAmountForUpdate) // 취소금액
										.vat(cancelAvailableVatForUpdate) // 취소부가세
										.originalMid(mid)
										.encryptedCardInfo(paymentEntity.getEncryptedCardInfo())
										.build())
								.createFullText();
		// 취소데이터 생성
		CancellationEntity cancellationEntity = CancellationEntity.builder()
																.cid(cid)
																.cancelAmount(cancelAmount)
																.cancelVat(cancelVat)
																.canceledAt(LocalDateTime.now())
																.cancelFullText(cancelFullText)
																.build();
		List<CancellationEntity> canceledList = paymentEntity.getCanceledList();
		if (CollectionUtils.isEmpty(canceledList)) {
			canceledList = new ArrayList<>();
		}
		canceledList.add(cancellationEntity);
		
		paymentEntity.setCanceledList(canceledList);
		paymentEntity.setLastCanceledAt(cancellationEntity.getCanceledAt());
		paymentEntity.setCancelAvailableAmount(cancelAvailableAmountForUpdate);
		paymentEntity.setCancelAvailableVat(cancelAvailableVatForUpdate);
		paymentEntity.setStatusType(statusType);

		// DB update
		PaymentEntity entity = paymentMongoRepository.save(paymentEntity);
		
		if (log.isDebugEnabled()) {
			log.debug("##################### 취소요청 결과 START #####################");
			log.debug("관리번호 : " + mid);
			log.debug("결제상태 : " + statusType);
			log.debug("취소 관리번호 : " + cid);
			log.debug("카드사 취소 전문통신 : " + cancelFullText);
			log.debug("PaymentEntity : " + entity);
			log.debug("##################### 취소요청 결과 END #####################");
		}
		
		// redis key delete
		paymentRedisRepository.delete(mid);
		
		return CancelTuple.builder()
					.cid(cid)
					.amount(paymentEntity.getAmount())
					.vat(paymentEntity.getVat())
					.cancelAmount(cancelAmount)
					.cancelVat(cancelVat)
					.cancelAvailableAmount(paymentEntity.getCancelAvailableAmount())
					.cancelAvailableVat(paymentEntity.getCancelAvailableVat())
					.statusType(statusType)
					.approvedAt(paymentEntity.getApprovedAt())
					.canceledAt(cancellationEntity.getCanceledAt())
					.build();
	}

	/**
	 * 관리자번호로 payment document 조회
	 *
	 * @param mid
	 * @return
	 */
	private PaymentEntity selectPaymentEntity(String mid) {
		Optional<PaymentEntity> documents = paymentMongoRepository.findById(mid);
		documents.filter(Objects::nonNull).orElseThrow(() -> new PaymentApiException(HttpStatus.BAD_REQUEST, messageService.getMessage(MessageType.PAYMENT_ERROR_NODATA.getCode()), mid));

		PaymentEntity paymentEntity = documents.get();

		return paymentEntity;
	}

	/**
	 * 카드 기본정보로 AES256 암호화 구문 생성 및 조회
	 *
	 * @param cardNumber
	 * @param mmyy
	 * @param cvc
	 * @return
	 */
	private String getEncryptCardInfo(final String cardNumber, final String mmyy, final String cvc) {
		// validation
		if (StringUtils.isAllBlank(cardNumber, mmyy, cvc)) {
			throw new PaymentApiException(HttpStatus.BAD_REQUEST, messageService.getMessage(MessageType.PAYMENT_ERROR_CARDINFO.getCode()), cardNumber);
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

	/**
	 * AES256 카드 암호화 정보로 CardTuple 객체 생성 및 조회
	 *
	 * @param encryptedCardInfo
	 * @return
	 */
	private CardTuple getCardTuple(String encryptedCardInfo) {
		// validation
		if (StringUtils.isBlank(encryptedCardInfo)) {
			throw new PaymentApiException(HttpStatus.BAD_REQUEST, messageService.getMessage(MessageType.PAYMENT_ERROR_CARDINFO.getCode()));
		}

		// 복호화
		String[] splitedCardInfo = StringUtils.split(encryptorComponent.decrypt(encryptedCardInfo, secretKey), DelimiterType.CARD.getDelimiter());

		if (CollectionUtils.sizeIsEmpty(splitedCardInfo)) {
			throw new PaymentApiException(HttpStatus.BAD_REQUEST, messageService.getMessage(MessageType.PAYMENT_ERROR_CARDINFO.getCode()));
		}

		return CardTuple.builder()
						.cardNumber(splitedCardInfo[0])
						.mmyy(splitedCardInfo[1])
						.cvc(splitedCardInfo[2])
						.build();
	}

}
