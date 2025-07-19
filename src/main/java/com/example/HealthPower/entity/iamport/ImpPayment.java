package com.example.HealthPower.entity.iamport;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ImpPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String impUid;

    @Column(name = "merchant_uid", length = 100)
    private String merchantUid;

    private int paidAmount;

    private String payStatus;

    // Optional: 결제 일시, 결제 수단, 카드사 등 추가 가능
}
