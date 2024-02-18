package me.mocadev.couponcore.component;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mocadev.couponcore.model.event.CouponIssueCompleteEvent;
import me.mocadev.couponcore.service.CouponCacheService;

@Slf4j
@RequiredArgsConstructor
@Component
public class CouponEventListener {

	private final CouponCacheService couponCacheService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	void issueComplete(CouponIssueCompleteEvent event) {
		log.info("issue complete. cache refresh start couponId: {}", event.couponId());
		couponCacheService.putCouponCache(event.couponId());
		couponCacheService.putCouponLocalCache(event.couponId());
		log.info("issue complete. cache refresh end couponId: {}", event.couponId());
	}
}
