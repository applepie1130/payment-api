package com.payment.api.model.criteria;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Cancel criteria.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="CancelCriteria", description="결제취소 파라미터")
public class CancelCriteria implements Serializable {

	private static final long serialVersionUID = 6751638646117012363L;

	@Size(min=20, max=20, message="관리번호(mid)는 20자리 입니다.")
	@NotEmpty(message="관리번호(mid)는 필수 항목 입니다.")
	@ApiModelProperty(notes = "관리번호", name = "mid", required = true)
	private String mid;
	
	@NotNull(message = "취소금액(cancelAmount)은 필수 항목 입니다.")
	@DecimalMin(value = "100", message="취소금액(amount)을 확인해주세요.")
	@DecimalMax(value = "1000000000", message="취소금액(amount)을 확인해주세요.")
	@ApiModelProperty(notes = "취소금액", name = "cancelAmount", required = true)
	private BigDecimal cancelAmount;
	
	@DecimalMin(value = "0", message="취소부가가치세(cancelVat)을 확인해주세요.")
	@DecimalMax(value = "110000000", message="취소부가가치세(cancelVat)을 확인해주세요.")
	@ApiModelProperty(notes = "취소부가가치세", name = "cancelVat", required = false)
	private BigDecimal cancelVat;
	
}
