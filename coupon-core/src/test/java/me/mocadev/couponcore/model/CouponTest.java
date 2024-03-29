package me.mocadev.couponcore.model;

import static org.assertj.core.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CouponTest {

	@Test
	@DisplayName("발급 수량이 남아있다면 true를 반환한다.")
	void test() {
		// given
		Coupon coupon = Coupon.builder()
			.totalQuantity(100)
			.issuedQuantity(99)
			.build();

		// when
		boolean result = coupon.availableIssueQuantity();

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("발급 수량이 소진 되었다면 false를 반환한다.")
	void test2() {
		// given
		Coupon coupon = Coupon.builder()
			.totalQuantity(100)
			.issuedQuantity(100)
			.build();

		// when
		boolean result = coupon.availableIssueQuantity();

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("최대 발급 수량이 설정되어 있지 않다면  true를 반환한다.")
	void test3() {
		// given
		Coupon coupon = Coupon.builder()
			.issuedQuantity(100)
			.build();

		// when
		boolean result = coupon.availableIssueQuantity();

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("발급 기간이 시작되지 않았다면 false를 반환한다.")
	void availableIssueDate1() {
		// given
		Coupon coupon = Coupon.builder()
			.dateIssueStart(LocalDateTime.now().plusDays(1))
			.dateIssueEnd(LocalDateTime.now().plusDays(2))
			.build();

		// when
		boolean result = coupon.availableIssueDate();

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("발급 기간이 종료되면 false를 반환한다.")
	void availableIssueDate2() {
		// given
		Coupon coupon = Coupon.builder()
			.dateIssueStart(LocalDateTime.now().minusDays(2))
			.dateIssueEnd(LocalDateTime.now().minusDays(1))
			.build();

		// when
		boolean result = coupon.availableIssueDate();

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("발급 기간이면 true를 반환한다.")
	void availableIssueDate3() {
		// given
		Coupon coupon = Coupon.builder()
			.dateIssueStart(LocalDateTime.now().minusDays(1))
			.dateIssueEnd(LocalDateTime.now().plusDays(2))
			.build();

		// when
		boolean result = coupon.availableIssueDate();

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("쿠폰이 정상 발급 된다.")
	void issue1() {
		// given
		Coupon coupon = Coupon.builder()
			.totalQuantity(100)
			.issuedQuantity(99)
			.dateIssueStart(LocalDateTime.now().minusDays(1))
			.dateIssueEnd(LocalDateTime.now().plusDays(2))
			.build();

		// when
		coupon.issue();

		// then
		assertThat(coupon.getIssuedQuantity()).isEqualTo(100);
	}
	@Test
	@DisplayName("발급 수량을 초과하면 예외를 반환하다.")
	void issue2() {
		// given
		Coupon coupon = Coupon.builder()
			.totalQuantity(100)
			.issuedQuantity(100)
			.dateIssueStart(LocalDateTime.now().minusDays(1))
			.dateIssueEnd(LocalDateTime.now().plusDays(2))
			.build();

		// when & then
		assertThatThrownBy(coupon::issue)
		    .isInstanceOf(CouponIssueException.class)
			.hasMessageContaining(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY.name())
		;
	}

	@DisplayName("발급 기간이 종료되면 true를 반환한다.")
	@Test
	void test_5() {
		// given
		Coupon coupon = Coupon.builder()
			.totalQuantity(100)
			.issuedQuantity(0)
			.dateIssueStart(LocalDateTime.now().minusDays(2))
			.dateIssueEnd(LocalDateTime.now().minusDays(2))
			.build();

		// when
		boolean result = coupon.isIssueComplete();

		// then
		assertThat(result).isTrue();
	}

	@DisplayName("잔여 발급 가능 수량이 없다면 true를 반환한다.")
	@Test
	void test_6() {
		// given
		Coupon coupon = Coupon.builder()
			.totalQuantity(100)
			.issuedQuantity(100)
			.dateIssueStart(LocalDateTime.now().minusDays(2))
			.dateIssueEnd(LocalDateTime.now().plusDays(2))
			.build();

		// when
		boolean result = coupon.isIssueComplete();

		// then
		assertThat(result).isTrue();
	}

	@DisplayName("발급 기한과 수량이 요효하면 false를 반환한다.")
	@Test
	void test_7() {
		// given
		Coupon coupon = Coupon.builder()
			.totalQuantity(100)
			.issuedQuantity(0)
			.dateIssueStart(LocalDateTime.now().minusDays(2))
			.dateIssueEnd(LocalDateTime.now().plusDays(2))
			.build();

		// when
		boolean result = coupon.isIssueComplete();

		// then
		assertThat(result).isFalse();
	}
}
