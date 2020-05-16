package com.payment.api.util;

import com.payment.api.model.criteria.CardFullTextCriteria;
import com.payment.api.model.type.PayType;
import com.payment.api.model.type.fulltext.FullTextBodyType;
import com.payment.api.model.type.fulltext.FullTextHeaderType;

public class CardApproveFullText extends CardFullText {

	public CardApproveFullText(CardFullTextCriteria cardFullTextCriteria) {
		super(cardFullTextCriteria);
	}

	@Override
	protected String createHeader() {
		CardFullTextCriteria cardFullTextCriteria = getCardFullTextCriteria();
		
		FullTextHeaderType paymentType = FullTextHeaderType.PAYMENT_TYPE;
		FullTextHeaderType mid = FullTextHeaderType.MID;
		
		return new StringBuffer()
						.append(paymentType.getFillOperationType()
											.operate(PayType.PAYMENT.name(), paymentType.getSize()))
						.append(mid.getFillOperationType()
											.operate(cardFullTextCriteria.getMid(), mid.getSize()))
						.toString();
	}

	@Override
	protected String createBody() {
		CardFullTextCriteria cardFullTextCriteria = getCardFullTextCriteria();
		
		FullTextBodyType cardNo = FullTextBodyType.CARD_NO;
		FullTextBodyType installMonth = FullTextBodyType.INSTALL_MONTH;
		FullTextBodyType mmyy = FullTextBodyType.MMYY;
		FullTextBodyType cvc = FullTextBodyType.CVC;
		FullTextBodyType amount = FullTextBodyType.AMOUNT;
		FullTextBodyType vat = FullTextBodyType.VAT;
		FullTextBodyType originMid = FullTextBodyType.ORIGIN_MID;
		FullTextBodyType encryptedCardInfo = FullTextBodyType.ENCRYPTED_CARD_INFO;
		FullTextBodyType etc = FullTextBodyType.ETC;
		
		return new StringBuffer()
				.append(cardNo.getFillOperationType()
								.operate(cardFullTextCriteria.getCardNumber(), cardNo.getSize()))
				.append(installMonth.getFillOperationType()
								.operate(cardFullTextCriteria.getInstallMonth(), installMonth.getSize()))
				.append(mmyy.getFillOperationType()
								.operate(cardFullTextCriteria.getMmyy(), mmyy.getSize()))
				.append(cvc.getFillOperationType()
								.operate(cardFullTextCriteria.getCvc(), cvc.getSize()))
				.append(amount.getFillOperationType()
								.operate(cardFullTextCriteria.getAmount().toString(), amount.getSize()))
				.append(vat.getFillOperationType()
								.operate(cardFullTextCriteria.getVat().toString(), vat.getSize()))
				.append(originMid.getFillOperationType()
								.operate(cardFullTextCriteria.getOriginalMid(), originMid.getSize()))
				.append(encryptedCardInfo.getFillOperationType()
								.operate(cardFullTextCriteria.getEncryptedCardInfo(), encryptedCardInfo.getSize()))
				.append(etc.getFillOperationType()
								.operate(null, etc.getSize()))
				.toString();
	}
	
}