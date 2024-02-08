package me.mocadev.couponcore.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import me.mocadev.couponcore.model.CouponIssue;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssue, Long> {
}
