
package com.payment.api.model.criteria;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardFullTextCriteria implements Serializable {

	private static final long serialVersionUID = -6109044511651047999L;
	
	private String mid; // 관리번호 
	private String cardNumber; // 카드번호 
	private String installMonth; // 할부개월수
	private String mmyy; // 카드유효기간
	private String cvc; // cvc 
	private BigDecimal amount; // 거래금액 
	private BigDecimal vat; // 부가가치세 
	private String originalMid; // 원거래관리번호 
	private String encryptedCardInfo; // 암호화된카드정보
	
	public String getInstallMonth() {
		if ( this.installMonth.length() == 1 ) {
			return new StringBuffer()
						.append("0")
						.append(this.installMonth)
						.toString();
		}
		
		return this.installMonth;
	}
}
