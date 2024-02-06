package me.mocadev.couponcore.model;

import static me.mocadev.couponcore.model.ErrorCode.*;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "coupons")
public class Coupon extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private CouponType couponType;

	private Integer totalQuantity;

	@Column(nullable = false)
	private int issuedQuantity;

	@Column(nullable = false)
	private int discountAmount;

	@Column(nullable = false)
	private int minAvailableAmount;

	@Column(nullable = false)
	private LocalDateTime dateIssueStart;

	@Column(nullable = false)
	private LocalDateTime dateIssueEnd;

	public boolean availableIssueQuantity() {
		if (this.totalQuantity == null) {
			return true;
		}
		return this.totalQuantity > this.issuedQuantity;
	}

	public boolean availableIssueDate() {
		LocalDateTime now = LocalDateTime.now();
		return this.dateIssueStart.isBefore(now) && this.dateIssueEnd.isAfter(now);
	}

	public void issue() {
		if (!availableIssueQuantity()) {
			throw new CouponIssueException(INVALID_COUPON_ISSUE_QUANTITY,
				"발급 가능한 수량을 초과합니다. total : %s, issued: %s".formatted(totalQuantity, issuedQuantity));
		}
		if (!availableIssueDate()) {
			throw new CouponIssueException(INVALID_COUPON_ISSUE_DATE,
				"발급 가능한 일자가 아닙니다. request : %s, issueStart: %s, issueEnd: %s".formatted(LocalDateTime.now(), dateIssueStart, dateIssueEnd));
		}
		this.issuedQuantity++;
	}
}
