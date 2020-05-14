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
@ApiModel(value="CardTuple", description="카드정보")
public class CardTuple implements Serializable {

	private static final long serialVersionUID = 188216198951334667L;

	@ApiModelProperty(notes = "카드번호", name = "cardNumber", required = true)
	private String cardNumber;
	
	@ApiModelProperty(notes = "카드유효기간(mmyy)", name = "mmyy", required = true)
	private String mmyy;
	
	@ApiModelProperty(notes = "카드유효기간(cvc)", name = "cvc", required = true)
	private String cvc;
}
