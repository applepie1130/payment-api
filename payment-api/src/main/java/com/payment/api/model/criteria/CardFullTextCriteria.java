
package com.payment.api.model.criteria;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Card full text criteria.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardFullTextCriteria implements Serializable {

	private static final long serialVersionUID = -6109044511651047999L;
	
	@ApiModelProperty(notes = "관리번호", name = "mid", required = true)
	private String mid;
	
	@ApiModelProperty(notes = "카드번호", name = "cardNumber", required = true)
	private String cardNumber;
	
	@ApiModelProperty(notes = "할부개월수", name = "installMonth", required = true)
	private String installMonth;
	
	@ApiModelProperty(notes = "카드유효기간", name = "mmyy", required = true)
	private String mmyy;
	
	@ApiModelProperty(notes = "cvc", name = "cvc", required = true)
	private String cvc;
	
	@ApiModelProperty(notes = "거래금액", name = "amount", required = true)
	private BigDecimal amount;
	
	@ApiModelProperty(notes = "부가가치세", name = "vat", required = true)
	private BigDecimal vat;
	
	@ApiModelProperty(notes = "원거래관리번호", name = "originalMid", required = false)
	private String originalMid;
	
	@ApiModelProperty(notes = "암호화된카드정보", name = "encryptedCardInfo", required = true)
	private String encryptedCardInfo;

	/**
	 * Gets install month.
	 *
	 * @return the install month
	 */
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
