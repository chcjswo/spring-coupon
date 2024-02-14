package me.mocadev.couponcore.service;

import static me.mocadev.couponcore.util.couponRedisUtils.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import me.mocadev.couponcore.repository.redis.RedisRepository;

@RequiredArgsConstructor
@Service
public class CouponIssueRedisService {

	private final RedisRepository redisRepository;

	public boolean availableTotalIssueQuantity(Integer totalQuantity, long couponId) {
		if (totalQuantity == null) {
			return true;
		}
		String key = getIssueRequestKey(couponId);
		return totalQuantity > redisRepository.sCard(key);
	}

	public boolean availableUserIssueQuantity(long couponId, long userId) {
		String key = getIssueRequestKey(couponId);
		return !redisRepository.sIsMember(key, String.valueOf(userId));
	}
}
