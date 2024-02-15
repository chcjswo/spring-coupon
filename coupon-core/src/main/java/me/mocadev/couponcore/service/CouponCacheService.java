package me.mocadev.couponcore.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import me.mocadev.couponcore.model.Coupon;
import me.mocadev.couponcore.repository.redis.dto.CouponRedisEntity;

@RequiredArgsConstructor
@Service
public class CouponCacheService {

	private final CouponIssueService couponIssueService;

	@Cacheable(cacheNames = "coupon")
	public CouponRedisEntity getCouponCache(long couponId) {
		Coupon coupon = couponIssueService.findCoupon(couponId);
		return new CouponRedisEntity(coupon);
	}
}
