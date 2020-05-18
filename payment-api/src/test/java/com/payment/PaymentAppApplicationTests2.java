package com.payment;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.payment.api.model.entity.PaymentResponseEntity;
import com.payment.api.model.tuple.ApproveTuple;
import com.payment.configuration.PaymentAppApplication;

@DisplayName("사전과제 Test Case 2")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = { PaymentAppApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class PaymentAppApplicationTests2 {
	
	private final MockMvc mockMvc;
	private static String MID;
	
	@Autowired
	public PaymentAppApplicationTests2(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	@BeforeAll
	void setup() throws Exception {
		assertNotNull(mockMvc);
	}
	
	/**
	 * 결제요청
	 * 요청 파라미터 : 결제금액 20000, 부가세 909
	 * 기대결과 : 성공 
	 * 상세결과
	 *  - 결제상태인 금액 20000
	 * 	- 결제상태인 부가세 909
	 */
	@Test
	@Order(1)
	@DisplayName("결제요청")
	void approveTest() throws Exception {
		MvcResult result = mockMvc.perform(post("/v1/payment/approve")
									.contentType(MediaType.APPLICATION_JSON)
									.param("cardNumber", "1234567890123456")
									.param("mmyy", "1125")
									.param("cvc", "777")
									.param("installMonth", "0")
									.param("amount", "20000")
									.param("vat", "909"))
								.andDo(print())
								.andExpect(status().isOk())
								.andReturn();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		PaymentResponseEntity paymentResponseEntity = mapper.readValue(result.getResponse().getContentAsString(), PaymentResponseEntity.class);
		ApproveTuple approveTuple = mapper.convertValue(paymentResponseEntity.getResult(), ApproveTuple.class);
		MID = approveTuple.getMid();
	}
	
	/**
	 * 부분취소 요청 1
	 * 요청 파라미터 : 취소금액 10000, 취소부가세 0
	 * 기대결과 : 성공
	 * 상세결과
	 *  - 결제상태인 금액 10000
	 * 	- 결제상태인 부가세 909
	 */
	@Test
	@Order(2)
	@DisplayName("부분취소 요청 1")
	void cancelTest01() throws Exception {
		mockMvc.perform(post("/v1/payment/cancel")
				.contentType(MediaType.APPLICATION_JSON)
				.param("mid", MID)
				.param("cancelAmount", "10000")
				.param("cancelVat", "0")
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.result.cancelAvailableAmount", is(10000))) // 결제상태인 금액 (남은금액)
			.andExpect(MockMvcResultMatchers.jsonPath("$.result.cancelAvailableVat", is(909))); // 결체상태인 부가세 (남은부가세)
	}
	
	/**
	 * 부분취소 요청 2
	 * 요청 파라미터 : 취소금액 10000, 취소부가세 0
	 * 기대결과 : 실패
	 * 상세결과
	 * 	- 결제상태인 금액 10000
	 * 	- 결제상태인 부가세 909
	 */
	@Test
	@Order(3)
	@DisplayName("부분취소 요청 2")
	void cancelTest02() throws Exception {
		mockMvc.perform(post("/v1/payment/cancel")
				.contentType(MediaType.APPLICATION_JSON)
				.param("mid", MID)
				.param("cancelAmount", "10000")
				.param("cancelVat", "0")
			)
			.andDo(print())
			.andExpect(status().is4xxClientError());
	}
	
	/**
	 * 부분취소 요청 3
	 * 요청 파라미터 : 취소금액 10000, 취소부가세 909
	 * 기대결과 : 성공
	 * 상세결과
	 * 	- 결제상태인 금액 0
	 * 	- 결제상태인 부가세 0
	 */
	@Test
	@Order(4)
	@DisplayName("부분취소 요청 3")
	void cancelTest03() throws Exception {
		mockMvc.perform(post("/v1/payment/cancel")
				.contentType(MediaType.APPLICATION_JSON)
				.param("mid", MID)
				.param("cancelAmount", "10000")
				.param("cancelVat", "909")
			)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.result.cancelAvailableAmount", is(0))) // 결제상태인 금액 (남은금액)
		.andExpect(MockMvcResultMatchers.jsonPath("$.result.cancelAvailableVat", is(0))); // 결체상태인 부가세 (남은부가세)
	}
}