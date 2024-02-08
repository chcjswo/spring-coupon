package me.mocadev.couponcore.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import me.mocadev.couponcore.TestConfig;
import me.mocadev.couponcore.model.Coupon;
import me.mocadev.couponcore.model.CouponIssue;
import me.mocadev.couponcore.model.CouponIssueException;
import me.mocadev.couponcore.model.CouponType;
import me.mocadev.couponcore.model.ErrorCode;
import me.mocadev.couponcore.repository.mysql.CouponIssueJpaRepository;
import me.mocadev.couponcore.repository.mysql.CouponIssueRepository;
import me.mocadev.couponcore.repository.mysql.CouponJpaRepository;

class CouponIssueServiceTest extends TestConfig {

	@Autowired
	private CouponIssueService couponIssueService;

	@Autowired
	private CouponIssueJpaRepository couponIssueJpaRepository;

	@Autowired
	private CouponIssueRepository couponIssueRepository;

	@Autowired
	private CouponJpaRepository couponJpaRepository;

	@BeforeEach
	void clean() {
		couponJpaRepository.deleteAllInBatch();
		couponIssueJpaRepository.deleteAllInBatch();
	}

	@DisplayName("쿠폰 발급 내역이 존재하면 예외를 반환한다.")
	@Test
	void issue_1() {
		// given
		CouponIssue couponIssue = CouponIssue.builder()
			.couponId(1L)
			.userId(1L)
			.build();
		couponIssueJpaRepository.save(couponIssue);

		// when & then
		assertThatThrownBy(() -> couponIssueService.issue(couponIssue.getCouponId(), couponIssue.getUserId()))
			.isInstanceOf(CouponIssueException.class)
			.hasMessageContaining("");
	}

	@DisplayName("쿠폰 발급 내역이 존재하지 않으면 쿠폰을 발급한다.")
	@Test
	void issue_2() {
		// given
		long couponId = 1L;
		long userId = 1L;

		// when
		CouponIssue result = couponIssueService.saveCouponIssue(couponId, userId);

		// then
		assertThat(couponIssueJpaRepository.findById(result.getId())).isPresent();
	}

	@DisplayName("발급 수량, 기한 중복 발급에 문제가 없으면 쿠폰을 발급한다.")
	@Test
	void findCoupon_1() {
		// given
		long userId = 1L;
		Coupon coupon = Coupon.builder()
			.couponType(CouponType.FIRST_COME_FIRST_SERVED)
			.title("선착순 테스트 쿠폰")
			.totalQuantity(100)
			.issuedQuantity(0)
			.dateIssueStart(LocalDateTime.now().minusDays(1))
			.dateIssueEnd(LocalDateTime.now().plusDays(1))
			.build();
		couponJpaRepository.save(coupon);

		// when
		couponIssueService.issue(coupon.getId(), userId);

		// then
		Coupon result = couponJpaRepository.findById(coupon.getId()).orElseThrow();
		assertThat(result.getIssuedQuantity()).isEqualTo(1);

		CouponIssue issueResult = couponIssueRepository.findFirstCouponIssue(coupon.getId(), userId);
		assertThat(issueResult).isNotNull();
	}

	@DisplayName("발급 수량에 문제가 생기면 예외를 반환한다.")
	@Test
	void findCoupon_2() {
		// given
		long userId = 1L;
		Coupon coupon = Coupon.builder()
			.couponType(CouponType.FIRST_COME_FIRST_SERVED)
			.title("선착순 테스트 쿠폰")
			.totalQuantity(100)
			.issuedQuantity(100)
			.dateIssueStart(LocalDateTime.now().minusDays(1))
			.dateIssueEnd(LocalDateTime.now().plusDays(1))
			.build();
		couponJpaRepository.save(coupon);

		// when & then
		assertThatThrownBy(() ->couponIssueService.issue(coupon.getId(), userId))
			.isInstanceOf(CouponIssueException.class)
			.hasMessageContaining(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY.name())
		;
	}

	@DisplayName("발급 기간에 문제가 생기면 예외를 반환한다.")
	@Test
	void findCoupon_3() {
		// given
		long userId = 1L;
		Coupon coupon = Coupon.builder()
			.couponType(CouponType.FIRST_COME_FIRST_SERVED)
			.title("선착순 테스트 쿠폰")
			.totalQuantity(100)
			.issuedQuantity(1)
			.dateIssueStart(LocalDateTime.now().minusDays(2))
			.dateIssueEnd(LocalDateTime.now().minusDays(1))
			.build();
		couponJpaRepository.save(coupon);

		// when & then
		assertThatThrownBy(() ->couponIssueService.issue(coupon.getId(), userId))
			.isInstanceOf(CouponIssueException.class)
			.hasMessageContaining(ErrorCode.INVALID_COUPON_ISSUE_DATE.name())
		;
	}

	@DisplayName("중복 발급에 문제가 생기면 예외를 반환한다.")
	@Test
	void findCoupon_4() {
		// given
		long userId = 1L;
		Coupon coupon = Coupon.builder()
			.couponType(CouponType.FIRST_COME_FIRST_SERVED)
			.title("선착순 테스트 쿠폰")
			.totalQuantity(100)
			.issuedQuantity(1)
			.dateIssueStart(LocalDateTime.now().minusDays(1))
			.dateIssueEnd(LocalDateTime.now().plusDays(1))
			.build();
		couponJpaRepository.save(coupon);

		CouponIssue couponIssue = CouponIssue.builder()
			.couponId(coupon.getId())
			.userId(userId)
			.build();
		couponIssueJpaRepository.save(couponIssue);

		// when & then
		assertThatThrownBy(() ->couponIssueService.issue(coupon.getId(), userId))
			.isInstanceOf(CouponIssueException.class)
			.hasMessageContaining(ErrorCode.DUPLICATED_COUPON_ISSUE.name())
		;
	}
	@DisplayName("쿠폰이 존재하지 않는다면 예외를 반환한다.")
	@Test
	void findCoupon_5() {
		// given
		long couponId = 1L;
		long userId = 1L;

		// when & then
		assertThatThrownBy(() ->couponIssueService.issue(couponId, userId))
			.isInstanceOf(CouponIssueException.class)
			.hasMessageContaining(ErrorCode.COUPON_NOT_EXISTS.name())
		;
	}

	@Test
	void saveCouponIssue() {
	}
}
