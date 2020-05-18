package com.payment.api.model.tuple;

import java.io.Serializable;

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
@ApiModel(value="CardResponseTuple", description="응답용 카드정보")
public class CardResponseTuple implements Serializable {
	
	private static final long serialVersionUID = 5300488421377005315L;

	@ApiModelProperty(notes = "카드번호", name = "cardNumber", required = true)
	private String cardNumber;
	
	@ApiModelProperty(notes = "카드유효기간(mmyy)", name = "mmyy", required = true)
	private String mmyy;
	
	@ApiModelProperty(notes = "카드유효기간(cvc)", name = "cvc", required = true)
	private String cvc;
}
