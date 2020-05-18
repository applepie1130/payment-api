package com.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.payment.api.model.entity.PaymentResponseEntity;
import com.payment.api.model.type.TimeoutType;
import com.payment.api.repository.redis.PaymentRedisRepository;
import com.payment.configuration.PaymentAppApplication;

@DisplayName("결제요청 API Multi Thread Test Case")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = { PaymentAppApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class ApproveMultiThreadTests {
	
	private final MockMvc mockMvc;
	private final PaymentRedisRepository paymentRedisRepository;
	private final static String CARD_NO = "1234567890123456";
	
	@Autowired
	public ApproveMultiThreadTests(MockMvc mockMvc, PaymentRedisRepository paymentRedisRepository) {
		this.mockMvc = mockMvc;
		this.paymentRedisRepository = paymentRedisRepository;
	}

	@BeforeAll
	void setup() throws Exception {
		assertNotNull(mockMvc);
		assertNotNull(paymentRedisRepository);
		
		// 동일카드번호에 대해 동시결제 방지를 위한 key set
		paymentRedisRepository.set(CARD_NO, "1", TimeoutType.REDIS_APPROVE.getMilliseconds());
	}
	
	/**
	 * 결제요청 Multi Thread Test Case
	 * 요청 파라미터 : 동일카드번호
	 * 기대결과 : 실패
	 * 상세결과
	 *  - http status : 400
	 * 	- message : 동일 요청에 대해 아직 처리중입니다.
	 */
	@Test
	@RepeatedTest(10) // 10회 반복
	@DisplayName("결제요청")
	void approveTest() throws Exception {
		MvcResult result = mockMvc.perform(post("/v1/payment/approve")
									.contentType(MediaType.APPLICATION_JSON)
									.param("cardNumber", CARD_NO)
									.param("mmyy", "1125")
									.param("cvc", "777")
									.param("installMonth", "0")
									.param("amount", "11000")
									.param("vat", "1000"))
								.andDo(print())
								.andExpect(status().is4xxClientError()) // 400 error
								.andReturn();
		ObjectMapper mapper = new ObjectMapper();
		PaymentResponseEntity paymentResponseEntity = mapper.readValue(result.getResponse().getContentAsString(), PaymentResponseEntity.class);
		
		assertEquals("동일 요청에 대해 아직 처리중입니다.", paymentResponseEntity.getMessage());
	}
	
	@AfterAll
	void release() throws Exception {
		paymentRedisRepository.delete(CARD_NO);
	}
}



