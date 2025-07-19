package com.example.HealthPower.entity.iamport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreateRequest {
    private Long productId;
    private int quantity;
    private int amount;
    private String orderName;
}
