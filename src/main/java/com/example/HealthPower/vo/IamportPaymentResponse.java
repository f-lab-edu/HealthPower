package com.example.HealthPower.vo;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IamportPaymentResponse {
    private String impUid;
    @Column(name = "merchant_uid", length = 100)
    private String merchantUid;
    private int amount;
    private String status;
}
