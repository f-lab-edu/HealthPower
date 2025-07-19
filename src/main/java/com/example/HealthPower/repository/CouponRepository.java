package com.example.HealthPower.repository;

import com.example.HealthPower.entity.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Modifying
    @Query("update Coupon c set c.totalStock = c.totalStock - 1 where c.id = :id")
    void decreaseStock(@Param("id") long id);
}
