package com.payment.api.component;

import com.payment.api.advice.PaymentApiException;
import com.payment.api.model.type.MessageType;
import com.payment.api.service.MessageService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * The type Encryptor component. (AES256)
 */
@Log4j2
@Component
public class EncryptorComponent {

	private final MessageService messageService;

	/**
	 * Instantiates a new Encryptor component.
	 *
	 * @param messageService the message service
	 */
	@Autowired
	public EncryptorComponent(MessageService messageService) {
		this.messageService = messageService;
	}

	/**
	 * 메시지 암호화
	 *
	 * @param message
	 * @param key
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws UnsupportedEncodingException
	 */
	private String encryptMessage(String message, String key) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
	
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedMessage = cipher.doFinal(message.getBytes());
		
		return Base64.encodeBase64String(encryptedMessage);
		
	}

	/**
	 * 메시지 복호화
	 *
	 * @param encryptedMessage
	 * @param key
	 * @return
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws UnsupportedEncodingException
	 */
	private String decryptMessage(String encryptedMessage, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] clearMessage = cipher.doFinal(Base64.decodeBase64(encryptedMessage.getBytes()));

		return new String(clearMessage, "UTF-8");
		
	}

	/**
	 * Encrypt string.
	 *
	 * @param originalString the original string
	 * @param secretKey      the secret key
	 * @return the string
	 */
	public String encrypt(String originalString, String secretKey) {

		if (StringUtils.isBlank(originalString) || StringUtils.isBlank(secretKey)) {
			throw new PaymentApiException(HttpStatus.INTERNAL_SERVER_ERROR, messageService.getMessage(MessageType.PAYMENT_ERROR_DEFAULT.getCode()));
		}

		try {
			return this.encryptMessage(originalString, secretKey);
			
		} catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
			throw new PaymentApiException(HttpStatus.INTERNAL_SERVER_ERROR,messageService.getMessage(MessageType.PAYMENT_ERROR_DEFAULT.getCode()));
		}

	}

	/**
	 * Decrypt string.
	 *
	 * @param encryptedText the encrypted text
	 * @param secretKey     the secret key
	 * @return the string
	 */
	public String decrypt(String encryptedText, String secretKey) {
		
		if (StringUtils.isBlank(encryptedText) || StringUtils.isBlank(secretKey)) {
			throw new PaymentApiException(HttpStatus.INTERNAL_SERVER_ERROR, messageService.getMessage(MessageType.PAYMENT_ERROR_DEFAULT.getCode()));
		}

		try {
			return this.decryptMessage(encryptedText, secretKey);
			
		} catch (UnsupportedEncodingException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException e) {
			log.error(e.getMessage(), e);
			throw new PaymentApiException(HttpStatus.INTERNAL_SERVER_ERROR, messageService.getMessage(MessageType.PAYMENT_ERROR_DEFAULT.getCode()));
		}
	}

}