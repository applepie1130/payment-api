package com.payment.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.payment.BasePackageLocation;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = BasePackageLocation.class)
public class PaymentAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentAppApplication.class, args);
	}

}
