package me.mocadev.couponcore.repository.redis;

import me.mocadev.couponcore.model.CouponIssueException;
import me.mocadev.couponcore.model.ErrorCode;

public enum CouponIssueRequestCode {

	SUCCESS(1),
	DUPLICATED_COUPON_ISSUE(2),
	INVALID_COUPON_ISSUE_QUANTITY(3);

	CouponIssueRequestCode(int code) {

	}

	public static CouponIssueRequestCode find(String code) {
		int codeValue = Integer.parseInt(code);
		return switch (codeValue) {
			case 1 -> SUCCESS;
			case 2 -> DUPLICATED_COUPON_ISSUE;
			case 3 -> INVALID_COUPON_ISSUE_QUANTITY;
			default -> throw new IllegalArgumentException("존재하지 않는 코드입니다.");
		};
	}

	public static void checkRequestResult(CouponIssueRequestCode code) {
		if (code == INVALID_COUPON_ISSUE_QUANTITY) {
			throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY, "발급 수량 초과");
		}
		if (code == DUPLICATED_COUPON_ISSUE) {
			throw new CouponIssueException(ErrorCode.DUPLICATED_COUPON_ISSUE, "이미 발급 요청된 쿠폰");
		}
	}
}
