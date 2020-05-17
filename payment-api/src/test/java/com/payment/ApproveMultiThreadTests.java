package com.payment;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.payment.api.model.entity.PaymentResponseEntity;
import com.payment.api.model.tuple.ApproveTuple;
import com.payment.configuration.PaymentAppApplication;

@DisplayName("결제요청 API Multi Thread Test Case")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = { PaymentAppApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class ApproveMultiThreadTests {
	
	@Autowired
	private MockMvc mockMvc;

	@BeforeAll
	void setup() throws Exception {
		assertNotNull(mockMvc);
	}
	
	/**
	 * 결제요청 Multi Thread Test Case
	 * 요청 파라미터 : 결제금액 11000, 부가세 1000
	 * 기대결과 : 1건만 성공
	 * 상세결과
	 *  - 결제상태인 금액 11000
	 * 	- 결제상태인 부가세 1000
	 */
	@Test
	@DisplayName("결제요청")
	void approveTest() throws Exception {
		mockMvc.perform(post("/v1/payment/approve")
						.contentType(MediaType.APPLICATION_JSON)
						.param("cardNumber", "1234567890123456")
						.param("mmyy", "1125")
						.param("cvc", "777")
						.param("installMonth", "0")
						.param("amount", "11000")
						.param("vat", "1000"))
					.andDo(print())
					.andExpect(status().isOk());
	}
}



