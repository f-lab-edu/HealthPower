package com.example.repository;

import com.example.entity.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Modifying
    @Query("update Coupon c set c.totalStock = c.totalStock - 1 where c.id = :id")
    void decreaseStock(@Param("id") long id);
}
