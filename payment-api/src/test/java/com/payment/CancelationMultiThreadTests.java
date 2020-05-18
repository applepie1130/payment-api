package com.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
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
import com.payment.api.model.tuple.ApproveTuple;
import com.payment.api.model.type.TimeoutType;
import com.payment.api.repository.redis.PaymentRedisRepository;
import com.payment.configuration.PaymentAppApplication;

@DisplayName("결제취소요청 API Multi Thread Test Case")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = { PaymentAppApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class CancelationMultiThreadTests {
	
	private final MockMvc mockMvc;
	private final PaymentRedisRepository paymentRedisRepository;
	private static String MID;
	
	@Autowired
	public CancelationMultiThreadTests(MockMvc mockMvc, PaymentRedisRepository paymentRedisRepository) {
		this.mockMvc = mockMvc;
		this.paymentRedisRepository = paymentRedisRepository;
	}

	@BeforeAll
	void setup() throws Exception {
		assertNotNull(mockMvc);
		assertNotNull(paymentRedisRepository);
	}
	
	@Test
	@DisplayName("key set")
	void setKey() throws Exception {
		MvcResult result = mockMvc.perform(post("/v1/payment/approve")
									.contentType(MediaType.APPLICATION_JSON)
									.param("cardNumber", "1234567890123456")
									.param("mmyy", "1125")
									.param("cvc", "777")
									.param("installMonth", "0")
									.param("amount", "11000")
									.param("vat", "1000"))
								.andDo(print())
								.andExpect(status().isOk())
								.andReturn();
			
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		PaymentResponseEntity paymentResponseEntity = mapper.readValue(result.getResponse().getContentAsString(), PaymentResponseEntity.class);
		ApproveTuple approveTuple = mapper.convertValue(paymentResponseEntity.getResult(), ApproveTuple.class);
		MID = approveTuple.getMid();
		
		// 동일 관리번호에 대해 동시결제 방지를 위한 key set
		paymentRedisRepository.set(MID, "1", TimeoutType.REDIS_APPROVE.getMilliseconds());
	}
	
	/**
	 * 취소 요청 Multi Thread Test Case
	 * 요청 파라미터 : 동일 MID 
	 * 기대결과 : 실패
	 * 상세결과
	 *  - http status : 400
	 * 	- message : 동일 요청에 대해 아직 처리중입니다.
	 */
	@Test
	@RepeatedTest(10) // 10회 반복
	@DisplayName("취소요청")
	void cancelTest() throws Exception {
		MvcResult result = mockMvc.perform(post("/v1/payment/cancel")
									.contentType(MediaType.APPLICATION_JSON)
									.param("mid", MID)
									.param("cancelAmount", "6600")
									.param("cancelVat", "600")
								)
								.andDo(print())
								.andExpect(status().is4xxClientError()) // 400 error
								.andReturn();
		
		ObjectMapper mapper = new ObjectMapper();
		PaymentResponseEntity paymentResponseEntity = mapper.readValue(result.getResponse().getContentAsString(), PaymentResponseEntity.class);
		
		assertEquals("동일 요청에 대해 아직 처리중입니다.", paymentResponseEntity.getMessage());
	}
	
	@AfterAll
	void release() throws Exception {
		paymentRedisRepository.delete(MID);
	}
}