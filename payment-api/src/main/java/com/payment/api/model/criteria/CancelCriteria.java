package com.payment.api.model.criteria;

import java.io.Serializable;
import java.math.BigDecimal;

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
@ApiModel(value="CancelCriteria", description="결제취소 파라미터")
public class CancelCriteria implements Serializable {

	private static final long serialVersionUID = 6751638646117012363L;

	@ApiModelProperty(notes = "관리번호", name = "mid", required = true)
	private String mid;
	
	@ApiModelProperty(notes = "취소금액", name = "cancelAmount", required = true)
	private BigDecimal cancelAmount;
	
	@ApiModelProperty(notes = "취소 부가가치세", name = "cancelVat", required = false)
	private BigDecimal cancelVat;
	
}
