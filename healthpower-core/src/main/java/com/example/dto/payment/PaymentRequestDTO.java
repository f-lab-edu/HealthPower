package com.example.dto.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDTO {
    private String impUid;
    private String merchantUid;
}
