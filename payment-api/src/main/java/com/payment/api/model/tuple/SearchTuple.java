package com.payment.api.model.tuple;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="SearchTuple", description="결제정보")
public class SearchTuple implements Serializable {

	private static final long serialVersionUID = -6227901814655234223L;
	
	@ApiModelProperty(notes = "카드정보", name = "cardInfo", required = true)
	private CardTuple cardInfo;
	
	@ApiModelProperty(notes = "mid", name = "mid", required = true)
	private String mid;
	
	@ApiModelProperty(notes = "결제금액", name = "amount", required = true)
	private BigDecimal amount;
	
	@ApiModelProperty(notes = "부가세", name = "vat", required = true)
	private BigDecimal vat;
	
	@ApiModelProperty(notes = "취소된 총 금액", name = "totalCancelVat", required = true)
	private BigDecimal totalCanceledAmount;
	
	@ApiModelProperty(notes = "취소된 총 부가세", name = "totalCanceledVat", required = true)
	private BigDecimal totalCanceledVat;
	
	@ApiModelProperty(notes = "남은 취소 가능금액", name = "cancelAvailableAmount", required = true)
	private BigDecimal cancelAvailableAmount;
	
	@ApiModelProperty(notes = "결제승인시간", name = "approvedAt", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime approvedAt;
	
	@ApiModelProperty(notes = "결제취소시간", name = "canceledAt", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime canceledAt;
	
}
