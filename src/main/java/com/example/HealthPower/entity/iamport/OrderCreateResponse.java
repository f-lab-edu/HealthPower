package com.example.HealthPower.entity.iamport;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCreateResponse {
    private String merchantUid;
    private int amount;
}
