package com.example.repository;

import com.example.entity.User;
import com.example.entity.coupon.Coupon;
import com.example.entity.coupon.CouponIssuance;
import com.example.enumpackage.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponIssuanceRepository extends JpaRepository<CouponIssuance, Long> {
    // 별도 작성 필요 없음: saveAll() 사용
    boolean existsByCouponIdAndUserId(long couponId, User user);

    List<CouponIssuance> findAllByExpiredAtBeforeAndIsExpiredFalse(LocalDateTime now);

    boolean existsByCouponAndUser(Coupon coupon, User user);

    List<CouponIssuance> findAllByUserAndStatus(User user, CouponStatus active);
}
