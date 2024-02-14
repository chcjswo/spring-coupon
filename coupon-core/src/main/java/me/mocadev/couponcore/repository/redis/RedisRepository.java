package me.mocadev.couponcore.repository.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class RedisRepository {

	private final RedisTemplate<String, String> redisTemplate;

	public Boolean zAdd(String key, String value, double score) {
		return redisTemplate.opsForZSet().addIfAbsent(key, value, score);
	}

	public Long sAdd(String key, String value) {
		return redisTemplate.opsForSet().add(key, value);
	}

	public Long sCard(String key) {
		return redisTemplate.opsForSet().size(key);
	}

	public Boolean sIsMember(String key, String value) {
		return redisTemplate.opsForSet().isMember(key ,value);
	}
}
