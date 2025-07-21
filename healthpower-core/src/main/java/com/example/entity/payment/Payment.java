package com.example.entity.payment;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; //결제한 아이디
    private String paymentKey; // Toss에서 발급한 결제 키
    private Long productId;

    @Column(unique = true)
    private String orderId; // 주문 ID

    private String orderName; // 주문 제품명
    private Long amount; // 결제 금액
    private String status; // READY / DONE / CANCELED 등
    private String method;  // 카드 / 가상계좌 / 간편결제 등
    private LocalDateTime paidAt; // 결제 완료 시각
    private String failReason; //실패 사유

    private int quantity; //Redis용 수량 필드
}
