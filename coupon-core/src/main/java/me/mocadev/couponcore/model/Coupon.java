package me.mocadev.couponcore.model;

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
			throw new RuntimeException("수량 검증 실패");
		}
		if (!availableIssueDate()) {
			throw new RuntimeException("기한 검증 실패");
		}
		this.issuedQuantity++;
	}
}
