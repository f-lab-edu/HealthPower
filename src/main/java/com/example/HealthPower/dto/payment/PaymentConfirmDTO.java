package com.example.HealthPower.dto.payment;

import lombok.Getter;

@Getter
public class PaymentConfirmDTO {
    private Double amount;
    private String orderId;
    private String paymentKey;
}
