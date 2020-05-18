package com.payment.api.util;

import com.payment.api.model.criteria.CardFullTextCriteria;
import com.payment.api.model.type.fulltext.FullTextHeaderType;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The type Card full text.
 */
@Data
@AllArgsConstructor
public abstract class CardFullText {
	
	private CardFullTextCriteria cardFullTextCriteria;

	/**
	 * Create full text string.
	 *
	 * @return the string
	 */
	public final String createFullText() {

		String header = createHeader();
		String body = createBody();
		
		int totalLength = header.length() + body.length();
		FullTextHeaderType fullTextLength = FullTextHeaderType.FULL_TEXT_LENGTH;

		return new StringBuffer()
					.append(fullTextLength.getFillOperationType()
										.operate(String.valueOf(totalLength), fullTextLength.getSize()))
					.append(header)
					.append(body)
					.toString();
		
	}

	/**
	 * Create header string.
	 *
	 * @return the string
	 */
	protected abstract String createHeader();

	/**
	 * Create body string.
	 *
	 * @return the string
	 */
	protected abstract String createBody();
	
}