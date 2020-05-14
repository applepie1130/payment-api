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
@ApiModel(value="ApproveCriteria", description="결제요청 파라미터")
public class ApproveCriteria implements Serializable {

	private static final long serialVersionUID = -636191608556058886L;

	@ApiModelProperty(notes = "카드번호", name = "cardNumber", required = true)
	private String cardNumber;
	
	@ApiModelProperty(notes = "카드유효기간(mmyy)", name = "mmyy", required = true)
	private String mmyy;
	
	@ApiModelProperty(notes = "카드유효기간(cvc)", name = "cvc", required = true)
	private String cvc;
	
	@ApiModelProperty(notes = "할부개월 0~12 (0:일시불, 1~12)", name = "installMonth", required = true)
	private String installMonth;
	
	@ApiModelProperty(notes = "결제금액 (100원이상 , 10억이하)", name = "amount", required = true)
	private BigDecimal amount;
	
	@ApiModelProperty(notes = "부가가치세", name = "vat", required = false)
	private BigDecimal vat;
	
}
