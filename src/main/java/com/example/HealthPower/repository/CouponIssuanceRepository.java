package com.example.HealthPower.repository;

import com.example.HealthPower.entity.coupon.CouponIssuance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssuanceRepository extends JpaRepository<CouponIssuance, Long> {

    boolean existsByCouponIdAndUserId(long couponId, String userId);
}
