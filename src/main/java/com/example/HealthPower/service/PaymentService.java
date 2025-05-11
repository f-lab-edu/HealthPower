package com.example.HealthPower.service;

import com.example.HealthPower.dto.payment.PaymentResultDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.entity.payment.Payment;
import com.example.HealthPower.entity.payment.TransactionHistory;
import com.example.HealthPower.entity.payment.TransactionType;
import com.example.HealthPower.repository.PaymentRepository;
import com.example.HealthPower.repository.TransactionHistoryRepository;
import com.example.HealthPower.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

//토스 결제 요청
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Value("${toss.secret}")
    private String secretKey;

    private final RestClient restClient;

    private final PaymentRepository paymentRepository;

    private final UserRepository userRepository;

    private final TransactionHistoryRepository transactionHistoryRepository;

    //결제 성공 시 내역 저장
    public void savePaymentInfo(PaymentResultDTO paymentResultDTO) {

        Payment payment = Payment.builder()
                .userId(paymentResultDTO.getUserId())
                .paymentKey(paymentResultDTO.getPaymentKey())
                .orderId(paymentResultDTO.getOrderId())
                .orderName(paymentResultDTO.getOrderName())
                .amount(paymentResultDTO.getAmount())
                .status(paymentResultDTO.getStatus())
                .method(paymentResultDTO.getMethod())
                .paidAt(paymentResultDTO.getPaidAt())
                .build();

        paymentRepository.save(payment);
    }

    @Transactional //DB 저장 로직은 트랜잭션으로 묶어야 함
    public void approvePayment(String paymentKey, String orderId, Long amount, String orderName, String userId) {

        if (paymentRepository.existsByOrderId(orderId)) {
            log.warn("중복 결제 시도 됨 : {}", orderId);
            throw new IllegalArgumentException("이미 결제가 완료된 주문입니다.");
        }

        String url = "https://api.tosspayments.com/v1/payments/confirm";

        String authHeader = "Basic " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        Map<String, Object> payload = Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount
        );

        String response = restClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(String.class);

        log.info("결제 승인 응답: {}", response);

        Payment payment = Payment.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .orderName(orderName)
                .userId(userId)
                .amount(amount.intValue())
                .status("DONE")
                .method("카드") // Toss API 응답에서 method 추출하면 더 좋음
                .paidAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        //사용자 잔액 차감
        User user = userRepository.findByUserId(userId).orElseThrow();
        if (user.getBalance() < amount) throw new IllegalArgumentException("잔액 부족");

        user.setBalance(user.getBalance() - amount);
        userRepository.save(user);

        //트랜잭션 내역 저장
        transactionHistoryRepository.save(new TransactionHistory(
                userId,
                TransactionType.PAYMENT,
                amount,
                user.getBalance()
        ));
    }

    @Transactional
    public void saveFailedPayment(String code, String message, String orderId, String orderName, String userId) {
        Payment payment = Payment.builder()
                .orderId(orderId)
                .status("Fail")
                .orderName(orderName)
                .userId(userId)
                .paidAt(LocalDateTime.now())
                .failReason(code + " : " + message)
                .build();

        paymentRepository.save(payment);

    }

    public void processPayment(String userId, int paymentAmount) {
        User user = userRepository.findByUserId(userId).orElseThrow();

        if (user.getBalance() < paymentAmount) {
            throw new IllegalArgumentException("잔액 부족");
        }

        user.setBalance(user.getBalance() - paymentAmount);
        userRepository.save(user);
    }

    public void charge(String userId, Long amount) {
        User user = userRepository.findByUserId(userId).orElseThrow();
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);

        transactionHistoryRepository.save(new TransactionHistory(
                userId,
                TransactionType.CHARGE,
                amount,
                user.getBalance()
        ));
    }

    public void pay(String userId, Long amount) {
        User user = userRepository.findByUserId(userId).orElseThrow();
        if (user.getBalance() < amount) throw new IllegalArgumentException("잔액 부족");

        user.setBalance(user.getBalance() - amount);
        userRepository.save(user);

        transactionHistoryRepository.save(new TransactionHistory(
                userId,
                TransactionType.PAYMENT,
                amount,
                user.getBalance()
        ));
    }
}
