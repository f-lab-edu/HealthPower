package com.example.entity.iamport;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCreateResponse {
    @Column(name = "merchant_uid")
    private String merchantUid;

    private int amount;
}
