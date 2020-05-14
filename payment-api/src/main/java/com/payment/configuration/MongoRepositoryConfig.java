
package com.payment.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.client.MongoClient;
import com.payment.api.repository.BaseRepositoryPackageLocation;

@Configuration
@EnableMongoRepositories(basePackageClasses = BaseRepositoryPackageLocation.class)
public class MongoRepositoryConfig extends AbstractMongoClientConfiguration {
 
    @Bean
    MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

	@Override
	public MongoClient mongoClient() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getDatabaseName() {
		// TODO Auto-generated method stub
		return null;
	}
}