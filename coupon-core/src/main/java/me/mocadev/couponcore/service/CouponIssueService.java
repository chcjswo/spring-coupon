package me.mocadev.couponcore.service;

import static me.mocadev.couponcore.model.ErrorCode.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import me.mocadev.couponcore.model.Coupon;
import me.mocadev.couponcore.model.CouponIssue;
import me.mocadev.couponcore.model.CouponIssueException;
import me.mocadev.couponcore.model.event.CouponIssueCompleteEvent;
import me.mocadev.couponcore.repository.mysql.CouponIssueJpaRepository;
import me.mocadev.couponcore.repository.mysql.CouponIssueRepository;
import me.mocadev.couponcore.repository.mysql.CouponJpaRepository;

@RequiredArgsConstructor
@Service
public class CouponIssueService {

	private final CouponJpaRepository couponJpaRepository;
	private final CouponIssueJpaRepository couponIssueJpaRepository;
	private final CouponIssueRepository couponIssueRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional
	public void issue(long couponId, long userId) {
		Coupon coupon = findCouponWithLock(couponId);
		coupon.issue();
		saveCouponIssue(couponId, userId);
		publicCouponEvent(coupon);
	}

	public Coupon findCoupon(long couponId) {
		return couponJpaRepository.findById(couponId)
			.orElseThrow(() -> new CouponIssueException(COUPON_NOT_EXISTS, COUPON_NOT_EXISTS.message + " couponId: %s".formatted(couponId)));
	}

	private Coupon findCouponWithLock(long couponId) {
		return couponJpaRepository.findCouponWithLock(couponId)
			.orElseThrow(() -> new CouponIssueException(COUPON_NOT_EXISTS, COUPON_NOT_EXISTS.message + " couponId: %s".formatted(couponId)));
	}

	public CouponIssue saveCouponIssue(long couponId, long userId) {
		checkAlreadyIssuance(couponId, userId);
		CouponIssue couponIssue = CouponIssue.builder()
			.couponId(couponId)
			.userId(userId)
			.build();
		return couponIssueJpaRepository.save(couponIssue);
	}

	private void checkAlreadyIssuance(long couponId, long userId) {
		CouponIssue issue = couponIssueRepository.findFirstCouponIssue(couponId, userId);
		if (issue != null) {
			throw new CouponIssueException(DUPLICATED_COUPON_ISSUE, "쿠폰 중복 발급 couponId: %s, userId: %s".formatted(couponId, userId));
		}
	}

	private void publicCouponEvent(Coupon coupon) {
		if (coupon.isIssueComplete()) {
			applicationEventPublisher.publishEvent(new CouponIssueCompleteEvent(coupon.getId()));
		}
	}
}
