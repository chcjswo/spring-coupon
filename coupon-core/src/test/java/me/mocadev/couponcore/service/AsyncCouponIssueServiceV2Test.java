package me.mocadev.couponcore.service;

import static me.mocadev.couponcore.model.ErrorCode.*;
import static me.mocadev.couponcore.util.couponRedisUtils.*;
import static org.assertj.core.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.mocadev.couponcore.TestConfig;
import me.mocadev.couponcore.model.Coupon;
import me.mocadev.couponcore.model.CouponIssueException;
import me.mocadev.couponcore.model.CouponType;
import me.mocadev.couponcore.repository.mysql.CouponJpaRepository;
import me.mocadev.couponcore.repository.redis.dto.CouponIssueRequest;

class AsyncCouponIssueServiceV2Test extends TestConfig {

	@Autowired
	private AsyncCouponIssueServiceV2 service;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private CouponJpaRepository couponJpaRepository;

	@BeforeEach
	void clear() {
		Collection<String> keys = redisTemplate.keys("*");
		redisTemplate.delete(keys);
	}

	@DisplayName("쿠폰이 존재하지 않는다면 예외를 반환한다.")
	@Test
	void issue_1() {
		// given
		long couponId = 1;
		long userId = 1;

		// when & then
		assertThatThrownBy(() -> service.issue(couponId, userId))
		    .isInstanceOf(CouponIssueException.class)
		    .hasMessageContaining(COUPON_NOT_EXISTS.name());
	}

	@DisplayName("쿠폰 발급 - 발급 가능 수량이 존재하지 않는다면 예외를 반환한다.")
	@Test
	void issue_2() {
		// given
		long userId = 1000;
		Coupon coupon = Coupon.builder()
			.couponType(CouponType.FIRST_COME_FIRST_SERVED)
			.title("title")
			.totalQuantity(10)
			.issuedQuantity(0)
			.dateIssueStart(LocalDateTime.now().minusDays(1))
			.dateIssueEnd(LocalDateTime.now().plusDays(1))
			.build();
		Coupon savedCoupon = couponJpaRepository.save(coupon);
		Long couponId = savedCoupon.getId();
		IntStream.range(0, coupon.getTotalQuantity()).forEach(f ->
			redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(f))
		);

		// when & then
		assertThatThrownBy(() -> service.issue(couponId, userId))
			.isInstanceOf(CouponIssueException.class)
			.hasMessageContaining(INVALID_COUPON_ISSUE_QUANTITY.name());
	}

	@DisplayName("쿠폰 발급 - 이미 발급된 유저라면 예외를 반환한다.")
	@Test
	void issue_3() {
		// given
		long userId = 1;
		Coupon coupon = Coupon.builder()
			.couponType(CouponType.FIRST_COME_FIRST_SERVED)
			.title("title")
			.totalQuantity(10)
			.issuedQuantity(0)
			.dateIssueStart(LocalDateTime.now().minusDays(1))
			.dateIssueEnd(LocalDateTime.now().plusDays(1))
			.build();
		Coupon savedCoupon = couponJpaRepository.save(coupon);
		Long couponId = savedCoupon.getId();
		redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId));

		// when & then
		assertThatThrownBy(() -> service.issue(couponId, userId))
			.isInstanceOf(CouponIssueException.class)
			.hasMessageContaining(DUPLICATED_COUPON_ISSUE.name());
	}

	@DisplayName("쿠폰 발급 - 발급 기간이 아닌 경우 예외를 반환한다.")
	@Test
	void issue_4() {
		// given
		long userId = 1;
		Coupon coupon = Coupon.builder()
			.couponType(CouponType.FIRST_COME_FIRST_SERVED)
			.title("title")
			.totalQuantity(10)
			.issuedQuantity(0)
			.dateIssueStart(LocalDateTime.now().plusDays(1))
			.dateIssueEnd(LocalDateTime.now().plusDays(1))
			.build();
		Coupon savedCoupon = couponJpaRepository.save(coupon);
		Long couponId = savedCoupon.getId();
		redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId));

		// when & then
		assertThatThrownBy(() -> service.issue(couponId, userId))
			.isInstanceOf(CouponIssueException.class)
			.hasMessageContaining(INVALID_COUPON_ISSUE_DATE.name());
	}

	@DisplayName("쿠폰 발급 - 쿠폰 발급을 기록한다.")
	@Test
	void issue_5() {
		// given
		long userId = 1;
		Coupon coupon = Coupon.builder()
			.couponType(CouponType.FIRST_COME_FIRST_SERVED)
			.title("title")
			.totalQuantity(10)
			.issuedQuantity(0)
			.dateIssueStart(LocalDateTime.now().minusDays(1))
			.dateIssueEnd(LocalDateTime.now().plusDays(1))
			.build();
		Coupon savedCoupon = couponJpaRepository.save(coupon);
		Long couponId = savedCoupon.getId();

		// when
		service.issue(couponId, userId);

		// then
		Boolean result = redisTemplate.opsForSet().isMember(getIssueRequestKey(savedCoupon.getId()), String.valueOf(userId));

		assertThat(result).isTrue();
	}

	@DisplayName("쿠폰 발급 - 쿠폰 발급이 성공하면 쿠폰 발급 큐에 적재된다.")
	@Test
	void issue_6() throws JsonProcessingException {
		// given
		long userId = 1;
		Coupon coupon = Coupon.builder()
			.couponType(CouponType.FIRST_COME_FIRST_SERVED)
			.title("title")
			.totalQuantity(10)
			.issuedQuantity(0)
			.dateIssueStart(LocalDateTime.now().minusDays(1))
			.dateIssueEnd(LocalDateTime.now().plusDays(1))
			.build();
		Coupon savedCoupon = couponJpaRepository.save(coupon);
		Long couponId = savedCoupon.getId();
		CouponIssueRequest issueRequest = new CouponIssueRequest(couponId, userId);

		// when
		service.issue(couponId, userId);

		// then
		String savedIssueRequest = redisTemplate.opsForList().leftPop(getIssueRequestQueueKey());

		assertThat(savedIssueRequest).isEqualTo(new ObjectMapper().writeValueAsString(issueRequest));
	}
}
