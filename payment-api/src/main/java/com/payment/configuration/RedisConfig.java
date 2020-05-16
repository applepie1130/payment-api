
package com.payment.configuration;

import java.util.Optional;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import redis.embedded.RedisServer;

@Configuration
public class RedisConfig implements InitializingBean, DisposableBean {

	@Value("${spring.redis.port}")
	private int redisPort;
	
	private RedisServer redisServer;

	@Override
	public void destroy() throws Exception {
		Optional.ofNullable(redisServer).ifPresent(RedisServer::stop);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		redisServer = new RedisServer(redisPort);
		redisServer.start();
	}
}