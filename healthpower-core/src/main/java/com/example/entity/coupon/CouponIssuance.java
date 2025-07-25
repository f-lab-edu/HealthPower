package com.example.entity.coupon;

import com.example.entity.User;
import com.example.enumpackage.CouponStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
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
    @GeneratedValue
    private Long id;

    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "is_expired")
    private boolean isExpired;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne
    private User user;

    public CouponIssuance(Instant issuedAt, LocalDateTime expiredAt) {
        this.issuedAt = issuedAt;
        this.expiredAt = expiredAt;
        this.status = CouponStatus.ACTIVE;
    }

    public void markAsExpired() {
        this.status = CouponStatus.EXPIRED;
    }

    public void expire() {
        if (!isExpire()) {
            throw new IllegalStateException("아직 만료되지 않았습니다.");
        }

        this.status = CouponStatus.EXPIRED;
    }

    public boolean isExpire() {
        return expiredAt.isBefore(LocalDateTime.now());
    }
}
