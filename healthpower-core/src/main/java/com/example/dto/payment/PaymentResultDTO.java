package com.example.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PaymentResultDTO {
    private String userId;
    private String paymentKey;
    private String orderId;
    private String orderName;
    private Long amount;
    private String status;
    private String method;
    private LocalDateTime paidAt;
}
