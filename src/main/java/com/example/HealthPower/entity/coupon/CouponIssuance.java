package com.example.HealthPower.entity.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "couponissuance")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CouponIssuance {

    @Id
    @GeneratedValue
    private Long id;

    private long couponId;
    private String userId;
    private Instant issuedAt;

    public CouponIssuance(long couponId, String userId, Instant issuedAt) {
        this.couponId = couponId;
        this.userId = userId;
        this.issuedAt = issuedAt;
    }
}
