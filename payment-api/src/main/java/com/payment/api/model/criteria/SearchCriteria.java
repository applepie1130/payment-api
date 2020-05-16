package com.payment.api.model.criteria;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

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
@ApiModel(value="SearchCriteria", description="결제조회 파라미터")
public class SearchCriteria implements Serializable {

	private static final long serialVersionUID = -1183295094716040461L;

	@Size(min=20, max=20, message="관리번호(mid)는 20자리 입니다.")
	@NotEmpty(message="관리번호(mid)는 필수 항목 입니다.")
	@ApiModelProperty(notes = "관리번호", name = "mid", required = true)
	private String mid;
	
}
