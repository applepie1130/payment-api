package com.payment.api.model.tuple;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.payment.api.model.type.StatusType;

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
@ApiModel(value="CancelTuple", description="결제취소결과정보")
public class CancelTuple implements Serializable {

	private static final long serialVersionUID = 7821106780900273234L;

	@ApiModelProperty(notes = "취소관리번호", name = "cid", required = true)
	private String cid;
	
	@ApiModelProperty(notes = "결제금액", name = "amount", required = true)
	private BigDecimal amount;
	
	@ApiModelProperty(notes = "부가세", name = "vat", required = true)
	private BigDecimal vat;
	
	@ApiModelProperty(notes = "취소금액", name = "cancelAmount", required = true)
	private BigDecimal cancelAmount;
	
	@ApiModelProperty(notes = "취소부가세", name = "cancelVat", required = true)
	private BigDecimal cancelVat;
	
	@ApiModelProperty(notes = "남은 취소 가능 금액", name = "cancelAvailableAmount", required = true)
	private BigDecimal cancelAvailableAmount;
	
	@ApiModelProperty(notes = "남은 취소 가능 부가세", name = "cancelAvailableVat", required = true)
	private BigDecimal cancelAvailableVat;

	@ApiModelProperty(notes = "결제상태", name = "statusType", required = true)
	private StatusType statusType;
	
	@ApiModelProperty(notes = "결제승인시각", name = "approvedAt", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime approvedAt;
	
	@ApiModelProperty(notes = "결제취소시간", name = "canceledAt", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime canceledAt;
	
}