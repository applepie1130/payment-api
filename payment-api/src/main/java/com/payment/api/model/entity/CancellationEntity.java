package com.payment.api.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancellationEntity {

	private String seq;

	private BigDecimal cancelAmount;
	
	private BigDecimal cancelVat;

	private LocalDateTime canceledAt;
	
}
