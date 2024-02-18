package me.mocadev.couponcore.repository.redis.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import me.mocadev.couponcore.model.Coupon;
import me.mocadev.couponcore.model.CouponIssueException;
import me.mocadev.couponcore.model.CouponType;
import me.mocadev.couponcore.model.ErrorCode;

public record CouponRedisEntity(
	Long id,
	CouponType couponType,
	Integer totalQuantity,
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	LocalDateTime dateIssueStart,
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	LocalDateTime dateIssueEnd,
	boolean availableIssueQuantity
) {

	public CouponRedisEntity(Coupon coupon) {
		this(
			coupon.getId(),
			coupon.getCouponType(),
			coupon.getTotalQuantity(),
			coupon.getDateIssueStart(),
			coupon.getDateIssueEnd(),
			coupon.availableIssueQuantity()
		);
	}

	private boolean availableIssueDate() {
		LocalDateTime now = LocalDateTime.now();
		return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now);
	}

	public void checkIssuableCoupon() {
		if (!availableIssueQuantity) {
			throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY, "쿠폰 소진 완료");
		}
		if (!availableIssueDate()) {
			throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_DATE, "발급 가능한 일자가 아닙니다.");
		}
	}
}
