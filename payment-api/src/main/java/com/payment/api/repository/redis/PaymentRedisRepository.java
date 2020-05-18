package com.payment.api.repository.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * The type Payment redis repository.
 */
@Repository
public class PaymentRedisRepository { 

	private RedisTemplate<String, String> redisTemplate;

	/**
	 * Instantiates a new Payment redis repository.
	 *
	 * @param redisTemplate the redis template
	 */
	@Autowired
	public PaymentRedisRepository(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * Set boolean.
	 *
	 * @param key     the key
	 * @param value   the value
	 * @param seconds the seconds
	 * @return the boolean
	 */
	public Boolean set(String key, String value, Integer seconds) {
		ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
		return opsForValue.setIfAbsent(key, value, Duration.ofMillis(seconds));
	}

	/**
	 * Delete boolean.
	 *
	 * @param key the key
	 * @return the boolean
	 */
	public Boolean delete(String key) {
		return redisTemplate.delete(key);
	}
	
}
