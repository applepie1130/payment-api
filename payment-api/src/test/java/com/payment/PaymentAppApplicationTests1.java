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

@DisplayName("사전과제 Test Case 1")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = { PaymentAppApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class PaymentAppApplicationTests1 {
	
	private static String MID;
	
	@Autowired
	private MockMvc mockMvc;

	@BeforeAll
	void setup() throws Exception {
		assertNotNull(mockMvc);
	}
	
	/**
	 * 결제요청
	 * 요청 파라미터 : 결제금액 11000, 부가세 1000
	 * 기대결과 : 성공 
	 * 상세결과
	 *  - 결제상태인 금액 11000
	 * 	- 결제상태인 부가세 1000
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
	}
	
	/**
	 * 부분취소 요청 1
	 * 요청 파라미터 : 취소금액 1100, 취소부가세 100
	 * 기대결과 : 성공
	 * 상세결과
	 *  - 결제상태인 금액 9900
	 * 	- 결제상태인 부가세 900
	 */
	@Test
	@Order(2)
	@DisplayName("부분취소 요청 1")
	void cancelTest01() throws Exception {
		mockMvc.perform(post("/v1/payment/cancel")
				.contentType(MediaType.APPLICATION_JSON)
				.param("mid", MID)
				.param("cancelAmount", "1100")
				.param("cancelVat", "100")
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.result.cancelAvailableAmount", is(9900))) // 결제상태인 금액 (남은금액)
			.andExpect(MockMvcResultMatchers.jsonPath("$.result.cancelAvailableVat", is(900))); // 결체상태인 부가세 (남은부가세)
	}
	
	/**
	 * 부분취소 요청 2
	 * 요청 파라미터 : 취소금액 3300, 취소부가세 null
	 * 기대결과 : 성공
	 * 상세결과
	 * 	- 결제상태인 금액 6600
	 * 	- 결제상태인 부가세 600
	 */
	@Test
	@Order(3)
	@DisplayName("부분취소 요청 2")
	void cancelTest02() throws Exception {
		mockMvc.perform(post("/v1/payment/cancel")
				.contentType(MediaType.APPLICATION_JSON)
				.param("mid", MID)
				.param("cancelAmount", "3300")
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.result.cancelAvailableAmount", is(6600))) // 결제상태인 금액 (남은금액)
			.andExpect(MockMvcResultMatchers.jsonPath("$.result.cancelAvailableVat", is(600))); // 결체상태인 부가세 (남은부가세)
	}
	
	/**
	 * 부분취소 요청 3
	 * 요청 파라미터 : 취소금액 7000, 취소부가세 null
	 * 기대결과 : 실패
	 * 상세결과
	 * 	- 결제상태인 금액 6600
	 * 	- 결제상태인 부가세 600
	 */
	@Test
	@Order(4)
	@DisplayName("부분취소 요청 3")
	void cancelTest03() throws Exception {
		mockMvc.perform(post("/v1/payment/cancel")
				.contentType(MediaType.APPLICATION_JSON)
				.param("mid", MID)
				.param("cancelAmount", "7000")
			)
			.andDo(print())
			.andExpect(status().is4xxClientError());
	}
	
	/**
	 * 부분취소 요청 4
	 * 요청 파라미터 : 취소금액 6600, 취소부가세 700
	 * 기대결과 : 실패
	 * 상세결과
	 * 	- 결제상태인 금액 6600
	 * 	- 결제상태인 부가세 600
	 */
	@Test
	@Order(5)
	@DisplayName("부분취소 요청 4")
	void cancelTest04() throws Exception {
		mockMvc.perform(post("/v1/payment/cancel")
				.contentType(MediaType.APPLICATION_JSON)
				.param("mid", MID)
				.param("cancelAmount", "6600")
				.param("cancelVat", "700")
			)
			.andDo(print())
			.andExpect(status().is4xxClientError());
	}
	
	/**
	 * 부분취소 요청 5
	 * 요청 파라미터 : 취소금액 6600, 취소부가세 600
	 * 기대결과 : 성공
	 * 상세결과
	 * 	- 결제상태인 금액 0
	 * 	- 결제상태인 부가세 0
	 */
	@Test
	@Order(6)
	@DisplayName("부분취소 요청 5")
	void cancelTest05() throws Exception {
		mockMvc.perform(post("/v1/payment/cancel")
				.contentType(MediaType.APPLICATION_JSON)
				.param("mid", MID)
				.param("cancelAmount", "6600")
				.param("cancelVat", "600")
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.result.cancelAvailableAmount", is(0))) // 결제상태인 금액 (남은금액)
			.andExpect(MockMvcResultMatchers.jsonPath("$.result.cancelAvailableVat", is(0))); // 결체상태인 부가세 (남은부가세)
	}
	
	/**
	 * 부분취소 요청 6
	 * 요청 파라미터 : 취소금액 100, 취소부가세 null
	 * 기대결과 : 실패
	 * 상세결과
	 * 	- 결제상태인 금액 0
	 * 	- 결제상태인 부가세 0
	 */
	@Test
	@Order(7)
	@DisplayName("부분취소 요청 6")
	void cancelTest06() throws Exception {
		mockMvc.perform(post("/v1/payment/cancel")
				.contentType(MediaType.APPLICATION_JSON)
				.param("mid", MID)
				.param("cancelAmount", "100")
			)
			.andDo(print())
			.andExpect(status().is4xxClientError());
	}
}



