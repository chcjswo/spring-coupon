package me.mocadev.couponcore.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

	// @Value("${spring.data.redis.host}")
	// private String host;
	//
	// @Value("${spring.data.redis.port}")
	// private int port;
	//
	// @Bean
	// RedissonClient redissonClient() {
	// 	Config config = new Config();
	// 	String address = "redis://" + host + ":" + port;
	// 	config.useSingleServer().setAddress(address);
	// 	return Redisson.create(config);
	// }
}
