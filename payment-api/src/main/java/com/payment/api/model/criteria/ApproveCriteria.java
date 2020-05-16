package com.payment.api.model.criteria;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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

	@NotEmpty(message = "카드번호(cardNumber)는 필수 항목 입니다.")
	@Pattern(regexp = "\\d{10}(\\d{6})?", message="카드번호는(cardNumber)는 10~16자리 숫자형식 입니다.")	
	@ApiModelProperty(notes = "카드번호", name = "cardNumber", required = true)
	private String cardNumber;
	
	@NotEmpty(message = "카드유효기간(mmyy)는 필수 항목 입니다.")
	@Pattern(regexp = "(0[1-9]|1[0-2])(1[0-9]|[2-9][0-9])", message="카드유효기간(mmyy)의 형식을 확인해주세요.")
	@ApiModelProperty(notes = "카드유효기간(mmyy)", name = "mmyy", required = true)
	private String mmyy;
	
	@NotEmpty(message = "카드cvc(cvc)는 필수 항목 입니다.")
	@Pattern(regexp = "\\d{3}", message="카드cvc(cvc)의 형식을 확인해주세요.")
	@ApiModelProperty(notes = "카드cvc(cvc)", name = "cvc", required = true)
	private String cvc;
	
	@NotEmpty(message = "할부개월(installMonth)은 필수 항목 입니다.")
	@Pattern(regexp = "([0-9]|1[0-2])", message = "할부개월(installMonth)의 형식을 확인해주세요.")
	@ApiModelProperty(notes = "할부개월 0~12 (0:일시불, 1~12)", name = "installMonth", required = true)
	private String installMonth;

	@NotNull(message = "결제금액(amount)은 필수 항목 입니다.")
	@DecimalMin(value = "100", message="결제금액(amount)은 100원 이상입니다.")
	@DecimalMax(value = "1000000000", message="결제금액(amount)은 10억이하입니다.")
	@ApiModelProperty(notes = "결제금액 (100원이상 , 10억이하)", name = "amount", required = true)
	private BigDecimal amount;
	
	@DecimalMin(value = "0", message="부가가치세(vat)을 확인해주세요.")
	@DecimalMax(value = "110000000", message="부가가치세(vat)을 확인해주세요.")
	@ApiModelProperty(notes = "부가가치세(vat)", name = "vat", required = false)
	private BigDecimal vat;
	
}
