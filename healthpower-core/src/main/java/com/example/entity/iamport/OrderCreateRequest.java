package com.example.entity.iamport;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreateRequest {

    @Column(name = "product_id")
    private Long productId;

    private int quantity;
    private int amount;

    @Column(name = "order_name")
    private String orderName;

    @Column(name = "coupon_issuance_id")
    private Long couponIssuanceId; //사용자가 선택한 쿠폰 발급 ID
}
