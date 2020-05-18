package com.payment.api.model.tuple;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

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
	
	/**
	 * 응답용 결과로 컨버팅
	 * 카드번호 앞 6자리, 뒤 3자리 *표 마스킹
	 * 
	 * @return
	 */
	public CardResponseTuple convertResponseTuple() {
		
		int length = cardNumber.length();
		String front = StringUtils.substring(cardNumber, 0, 6);
		String rear = StringUtils.substring(cardNumber, cardNumber.length()-3, cardNumber.length());
		String masking = StringUtils.repeat("*", length - (front.length() + rear.length()) );
		
		String formatNumber = new StringBuffer()
								.append(front)
								.append(masking)
								.append(rear)
								.toString();
		
		return CardResponseTuple.builder()
								.cardNumber(formatNumber)
								.mmyy(mmyy)
								.cvc(cvc)
								.build();
	}
}
