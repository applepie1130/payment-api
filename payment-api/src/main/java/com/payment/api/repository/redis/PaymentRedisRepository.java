package com.payment.api.repository.redis;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRedisRepository { 

	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	public PaymentRedisRepository(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public Boolean set(String key, String value, Integer seconds) {
		ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
		return opsForValue.setIfAbsent(key, value, Duration.ofSeconds(seconds));
	}
	
	public Boolean delete(String key) {
		return redisTemplate.delete(key);
	}
	
}
