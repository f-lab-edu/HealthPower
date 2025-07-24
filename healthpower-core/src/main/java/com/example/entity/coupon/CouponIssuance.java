package com.example.entity.coupon;

import com.example.entity.User;
import com.example.enumpackage.CouponStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "couponissuance")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponIssuance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "expired_at")
    private Instant expiredAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "is_expired")
    private boolean isExpired;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public CouponIssuance(Instant issuedAt, Instant expiredAt) {
        this.issuedAt = issuedAt;
        this.expiredAt = expiredAt;
        this.status = CouponStatus.ACTIVE;
    }

    public void expire() {
        if (!isExpired()) {
            throw new IllegalStateException("아직 만료되지 않았습니다.");
        }
        markAsExpired();
    }

    public void markAsExpired() {
        this.status = CouponStatus.EXPIRED;
        this.isExpired = true;
    }

    public boolean isExpired() {
        return expiredAt.isBefore(Instant.now());
    }
}
