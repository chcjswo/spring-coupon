package me.mocadev.couponcore.service;

import static me.mocadev.couponcore.util.couponRedisUtils.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import me.mocadev.couponcore.model.CouponIssueException;
import me.mocadev.couponcore.model.ErrorCode;
import me.mocadev.couponcore.repository.redis.RedisRepository;
import me.mocadev.couponcore.repository.redis.dto.CouponRedisEntity;

@RequiredArgsConstructor
@Service
public class CouponIssueRedisService {

	private final RedisRepository redisRepository;

	public void checkCouponIssueQuantity(CouponRedisEntity coupon, long userId) {
		if (!availableTotalIssueQuantity(coupon.totalQuantity(), coupon.id())) {
			throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY, "발급 가능한 수량을 초과했습니다.");
		}
		if (!availableUserIssueQuantity(coupon.id(), userId)) {
			throw new CouponIssueException(ErrorCode.DUPLICATED_COUPON_ISSUE, "이미 발급 요청이 처리됐습니다.");
		}
	}

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
