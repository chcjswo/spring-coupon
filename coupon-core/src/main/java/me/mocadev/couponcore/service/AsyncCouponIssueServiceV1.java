package me.mocadev.couponcore.service;

import static me.mocadev.couponcore.util.couponRedisUtils.*;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.mocadev.couponcore.component.DistributeLockExecutor;
import me.mocadev.couponcore.model.Coupon;
import me.mocadev.couponcore.model.CouponIssueException;
import me.mocadev.couponcore.model.ErrorCode;
import me.mocadev.couponcore.repository.redis.RedisRepository;
import me.mocadev.couponcore.repository.redis.dto.CouponIssueRequest;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV1 {

	private final RedisRepository redisRepository;
	private final CouponIssueRedisService couponIssueRedisService;
	private final CouponIssueService couponIssueService;
	private final ObjectMapper objectMapper;
	private final DistributeLockExecutor distributeLockExecutor;

	public void issue(long couponId, long userId) {
		// 발급 요청 처리
		// String key = "issue.request.sorted_set.couponId=%s".formatted(couponId);
		// redisRepository.zAdd(key, String.valueOf(userId), System.currentTimeMillis());

		Coupon coupon = couponIssueService.findCoupon(couponId);
		if (!coupon.availableIssueDate()) {
			throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_DATE, "발급 가능한 일자가 아닙니다.");
		}
		distributeLockExecutor.execute("lock_%s".formatted(couponId), 3000, 3000, () -> {
			if (!couponIssueRedisService.availableTotalIssueQuantity(coupon.getTotalQuantity(), couponId)) {
				throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY, "발급 가능한 수량을 초과했습니다.");
			}
			if (!couponIssueRedisService.availableUserIssueQuantity(couponId, userId)) {
				throw new CouponIssueException(ErrorCode.DUPLICATED_COUPON_ISSUE, "이미 발급 요청이 처리됐습니다.");
			}
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
