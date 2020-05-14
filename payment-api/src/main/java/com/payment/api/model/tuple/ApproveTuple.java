package com.payment.api.model.tuple;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="ApproveTuple", description="결제요청결과정보")
public class ApproveTuple implements Serializable {

	private static final long serialVersionUID = -6227901814655234223L;

	@ApiModelProperty(notes = "mid", name = "mid", required = true)
	private String mid;
	
	@ApiModelProperty(notes = "결제금액", name = "amount", required = true)
	private BigDecimal amount;
	
	@ApiModelProperty(notes = "부가세", name = "vat", required = true)
	private BigDecimal vat;
	
	@ApiModelProperty(notes = "결제승인시간", name = "approvedAt", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime approvedAt;
	
}
