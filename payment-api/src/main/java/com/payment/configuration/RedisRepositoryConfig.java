//
//package com.payment.configuration;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
//
//import com.payment.api.repository.redis.BaseRedisRepositoryPackageLocation;
//
//@Configuration
//@EnableRedisRepositories(basePackageClasses = BaseRedisRepositoryPackageLocation.class)
//public class RedisRepositoryConfig {
//	
//	@Value("${spring.redis.host}")
//	private String redisHost;
//
//	@Value("${spring.redis.port}")
//	private int redisPort;
//
//	@Bean
//	public LettuceConnectionFactory redisConnectionFactory() {
//		return new LettuceConnectionFactory(redisHost, redisPort);
//	}
//
//	@Bean
//	public RedisTemplate<?, ?> redisTemplate() {
//		RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
//		redisTemplate.setConnectionFactory(redisConnectionFactory());
//		return redisTemplate;
//	}
//}