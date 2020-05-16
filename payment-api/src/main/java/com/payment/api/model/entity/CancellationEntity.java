package com.payment.api.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancellationEntity {
	
	@Transient
    public static final String SEQUENCE_NAME = "cancelations_sequence";

	private String cid;

	private BigDecimal cancelAmount;
	
	private BigDecimal cancelVat;
	
	private String cancelFullText;

	private LocalDateTime canceledAt;
	
}
