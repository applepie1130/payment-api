package com.payment.api.component;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.payment.api.advice.PaymentApiException;
import com.payment.api.model.type.MessageType;
import com.payment.api.service.MessageService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class EncryptorComponent {

	private final MessageService messageService;

	@Autowired
	public EncryptorComponent(MessageService messageService) {
		this.messageService = messageService;
	}

	private String encryptMessage(String message, String key) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
	
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedMessage = cipher.doFinal(message.getBytes());
		
		return Base64.encodeBase64String(encryptedMessage);
		
	}

	private String decryptMessage(String encryptedMessage, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] clearMessage = cipher.doFinal(Base64.decodeBase64(encryptedMessage.getBytes()));

		return new String(clearMessage, "UTF-8");
		
	}

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