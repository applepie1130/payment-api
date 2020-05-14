package com.payment.api.model.entity;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="PaymentResponseEntity", description="결제응답 엔티티")
public class PaymentResponseEntity implements Serializable {
	
	private static final long serialVersionUID = 2193961797882291917L;

	@ApiModelProperty(notes = "Http Status Code", name = "status", required = true)
	private HttpStatus status;
	
	@ApiModelProperty(notes = "API 응답메시지", name = "message", required = true)
	private String message;
	
	@ApiModelProperty(notes = "API 결과 ", name = "result", required = false)
	private Object result;

}
