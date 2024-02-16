package me.mocadev.couponcore.service;

import static me.mocadev.couponcore.util.couponRedisUtils.*;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.mocadev.couponcore.component.DistributeLockExecutor;
import me.mocadev.couponcore.model.CouponIssueException;
import me.mocadev.couponcore.model.ErrorCode;
import me.mocadev.couponcore.repository.redis.RedisRepository;
import me.mocadev.couponcore.repository.redis.dto.CouponIssueRequest;
import me.mocadev.couponcore.repository.redis.dto.CouponRedisEntity;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV2 {

	private final RedisRepository redisRepository;
	private final CouponCacheService couponCacheService;

	public void issue(long couponId, long userId) {
		CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);
		coupon.checkIssuableCoupon();
		issueRequest(couponId, userId, coupon.totalQuantity());
	}

	private void issueRequest(long couponId, long userId, Integer totalIssueQuantity) {
		if (totalIssueQuantity == null) {
			redisRepository.issueRequest(couponId, userId, Integer.MAX_VALUE);
		}
		redisRepository.issueRequest(couponId, userId, totalIssueQuantity);
	}
}
