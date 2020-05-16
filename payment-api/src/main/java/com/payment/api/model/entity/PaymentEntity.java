package com.payment.api.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "payments")
public class PaymentEntity {

    @Transient
    public static final String SEQUENCE_NAME = "payments_sequence";
	
	@Id	
	private String mid;

	private BigDecimal amount;
	
	private BigDecimal vat;
	
	private BigDecimal cancelAvailableAmount;
	
	private BigDecimal cancelAvailableVat;

	private String encryptedCardInfo;
	
	private String fullText;
	
	private List<CancellationEntity> canceledList;
	
	private LocalDateTime approvedAt;

	private LocalDateTime lastCanceledAt;
	
}
