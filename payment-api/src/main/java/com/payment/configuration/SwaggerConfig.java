package com.payment.configuration;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.base.Predicate;

import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	@Bean
	public Docket api() {
		Docket docket = new Docket(DocumentationType.SWAGGER_2);
		Predicate<RequestHandler> basePackage = RequestHandlerSelectors.basePackage("todo.api.controller");
		
		ArrayList<ResponseMessage> responseMessageStatus = newArrayList( 
				new ResponseMessageBuilder().code(400).message("Invalid request").build(),
				new ResponseMessageBuilder().code(401).message("No permission").build(),
				new ResponseMessageBuilder().code(500).message("Error request").build()
        );
		
		return docket.select()
				.apis(basePackage)
				.paths(PathSelectors.any())
				.build()
				.apiInfo(apiInfo())
				.globalResponseMessage(RequestMethod.GET, responseMessageStatus)
				.globalResponseMessage(RequestMethod.POST, responseMessageStatus)
				.globalResponseMessage(RequestMethod.PUT, responseMessageStatus)
				.globalResponseMessage(RequestMethod.DELETE, responseMessageStatus)
				.useDefaultResponseMessages(false);
	}
	
	/**
	 * API Documents Information
	 * @return
	 */
	private ApiInfo apiInfo() {
		
		String title = "TODO-API Documents";
		String version = "0.0.1";
		String license = "Sungjun All rights reserved";
		
		return new ApiInfoBuilder()
				.title(title)
				.version(version)
				.license(license)
				.build();
	}
}