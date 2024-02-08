package me.mocadev.couponcore.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import me.mocadev.couponcore.model.Coupon;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
}
