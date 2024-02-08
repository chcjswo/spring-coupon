package me.mocadev.couponcore.repository.mysql;

import static me.mocadev.couponcore.model.QCouponIssue.*;
import org.springframework.stereotype.Repository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.mocadev.couponcore.model.CouponIssue;

@RequiredArgsConstructor
@Repository
public class CouponIssueRepository {

	private final JPAQueryFactory queryFactory;

	public CouponIssue findFirstCouponIssue(long couponId, long userId) {
		return queryFactory.selectFrom(couponIssue)
			.where(couponIssue.couponId.eq(couponId))
			.where(couponIssue.userId.eq(userId))
			.fetchFirst();
	}
}
