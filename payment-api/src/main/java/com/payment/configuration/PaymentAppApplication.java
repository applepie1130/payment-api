package com.payment.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.payment.BasePackageLocation;
import com.payment.api.repository.mongo.BaseMongoRepositoryPackageLocation;

@SpringBootApplication
@EnableAutoConfiguration
@EnableMongoRepositories(basePackageClasses = BaseMongoRepositoryPackageLocation.class)
@ComponentScan(basePackageClasses = BasePackageLocation.class)
public class PaymentAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentAppApplication.class, args);
	}

}
