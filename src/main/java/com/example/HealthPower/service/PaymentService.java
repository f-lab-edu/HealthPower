package com.example.HealthPower.service;

import com.example.HealthPower.dto.payment.PaymentResultDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.entity.board.Product;
import com.example.HealthPower.entity.payment.Payment;
import com.example.HealthPower.entity.payment.TransactionHistory;
import com.example.HealthPower.entity.payment.TransactionType;
import com.example.HealthPower.repository.PaymentRepository;
import com.example.HealthPower.repository.ProductRepository;
import com.example.HealthPower.repository.TransactionHistoryRepository;
import com.example.HealthPower.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//토스 결제 요청
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Value("${toss.secret}")
    private String secretKey;

    private final PaymentLogService paymentLogService;
    private final RestClient restClient;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final ProductRepository productRepository;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

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

    //Redis에 결제정보 저장
    public void savePaymentInfoRedis(String orderId, String userId, int quantity, String orderName, Long productId) {
        Map<String, Object> info = Map.of(
                "userId", userId,
                "orderId", orderId,
                "quantity", quantity,
                "orderName", orderName,
                "productId", productId
        );

        try {
            String json = objectMapper.writeValueAsString(info);
            redisTemplate.opsForValue().set("Order : " + orderId, json, 10, TimeUnit.MINUTES);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 저장 실패", e);
        }
    }

    //Redis 결제정보 불러오기
    public Map<String, Object> getOrderInfoRedis(String orderId) {
        String json = redisTemplate.opsForValue().get("Order : " + orderId);
        if (json == null) {
            log.error("Redis에 해당 주문 정보 없음 - orderId: {}", orderId);
            throw new IllegalArgumentException("해당 주문 정보를 찾을 수 없습니다.");
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 파싱 실패", e);
        }
    }

    public void deleteOrderInfoRedis(String orderId) {
        redisTemplate.delete("Order : " + orderId);
    }

    @Transactional //DB 저장 로직은 트랜잭션으로 묶어야 함
    public void approvePayment(String paymentKey, String orderId, Long amount, String userId) {

        if (paymentRepository.existsByOrderId(orderId)) {
            log.warn("중복 결제 시도 됨 : {}", orderId);
            throw new IllegalArgumentException("이미 결제가 완료된 주문입니다.");
        }

        //redis에서 결제 정보 조회
        Map<String, Object> orderInfo = getOrderInfoRedis(orderId);
        int quantity = Integer.parseInt(orderInfo.get("quantity").toString());
        String orderName = orderInfo.get("orderName").toString();

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

        //사용자 조회
        User user = userRepository.findByUserId(userId).orElseThrow();

        //상품조회
        Product product = productRepository.findByProductName(orderName)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품 없음"));

        //총 금액 계산
        double totalPrice = (double) product.getPrice() * quantity;

        product.decreaseStock(quantity);

        if (user.getBalance() < totalPrice) {
            //결제 실패 로그 DB 저장
            paymentLogService.logFailure(userId, amount, user.getBalance(), product, quantity);
            log.warn("결제 실패 - 잔액 부족. userId={}, balance={}, amount={}", userId, user.getBalance(), amount);

            throw new IllegalArgumentException("잔액 부족");

        }

        // 잔액 차감
        user.setBalance(user.getBalance() - totalPrice);
        userRepository.save(user);

        //트랜잭션 내역 저장
        transactionHistoryRepository.save(new TransactionHistory(
                userId,
                TransactionType.PAYMENT,
                totalPrice,
                user.getBalance(),
                product.getProductName(),
                quantity
        ));

        //redis에서 orderId 삭제
        deleteOrderInfoRedis(orderId);
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
}
