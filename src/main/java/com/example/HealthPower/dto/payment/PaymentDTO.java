package com.example.HealthPower.dto.payment;

import lombok.Getter;

@Getter
public class PaymentDTO {
    private String userId;
    private Double amount;
    private String orderId;
    private String orderName;
    private String cardNumber;
    private String cardExpirationYear;
    private String cardExpirationMonth;
    private String cardPassword;
    private String customerIdentityNumber; // 주민번호 앞 6자리 or 사업자번호
}
