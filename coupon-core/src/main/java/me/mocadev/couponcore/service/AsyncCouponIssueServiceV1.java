package me.mocadev.couponcore.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import me.mocadev.couponcore.repository.redis.RedisRepository;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV1 {

	private final RedisRepository redisRepository;

	public void issue(long couponId, long userId) {
		// 발급 요청 처리
		// String key = "issue.request.sorted_set.couponId=%s".formatted(couponId);
		// redisRepository.zAdd(key, String.valueOf(userId), System.currentTimeMillis());


	}
}
