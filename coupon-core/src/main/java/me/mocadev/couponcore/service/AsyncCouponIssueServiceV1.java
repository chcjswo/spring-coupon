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
public class AsyncCouponIssueServiceV1 {

	private final RedisRepository redisRepository;
	private final CouponIssueRedisService couponIssueRedisService;
	private final ObjectMapper objectMapper;
	private final DistributeLockExecutor distributeLockExecutor;
	private final CouponCacheService couponCacheService;

	public void issue(long couponId, long userId) {
		// 발급 요청 처리
		// String key = "issue.request.sorted_set.couponId=%s".formatted(couponId);
		// redisRepository.zAdd(key, String.valueOf(userId), System.currentTimeMillis());

		CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);
		coupon.checkIssuableCoupon();
		distributeLockExecutor.execute("lock_%s".formatted(couponId), 3000, 3000, () -> {
			couponIssueRedisService.checkCouponIssueQuantity(coupon, userId);
			issueRequest(couponId, userId);
		});
	}

	private void issueRequest(long couponId, long userId) {
		CouponIssueRequest issueRequest = new CouponIssueRequest(couponId, userId);
		try {
			String value = objectMapper.writeValueAsString(issueRequest);
			redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(userId));
			redisRepository.rPush(getIssueRequestQueueKey(), value);
		} catch (JsonProcessingException e) {
			throw new CouponIssueException(ErrorCode.FAIL_COUPON_ISSUE_REQUEST, "request: %s".formatted(issueRequest));
		}
	}
}
