package com.example.entity.iamport;

import com.example.entity.coupon.CouponIssuance;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ImpOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "merchant_uid")
    private String merchantUid;

    @Column(name = "order_name")
    private String orderName;

    private int quantity;

    private int amount;

    private String status; // PENDING, PAID, FAILED 등

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_issuance_id")
    private CouponIssuance couponIssuance;

}
