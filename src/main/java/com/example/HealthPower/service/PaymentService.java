package com.example.HealthPower.service;

import com.example.HealthPower.dto.UserDTO;
import com.example.HealthPower.dto.payment.PaymentDTO;
import com.example.HealthPower.dto.payment.PaymentResultDTO;
import com.example.HealthPower.entity.payment.Payment;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.loginUser.LoginUser;
import com.example.HealthPower.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

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

    //private final RestTemplate restTemplate;

    private final RestClient restClient;

    private final PaymentRepository paymentRepository;

    //결제 요청
    /*public ResponseEntity<String> requestPayment(String userId, PaymentDTO paymentDTO) {

        String url = "https://api.tosspayments.com/v1/payments/key-in"; //백엔드로만 구현해야하기 때문에 key-in 방식으로 구현

        // 1. Header 세팅
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. Basic auth 설정
        headers.setBasicAuth(secretKey, "");

        // 3. 요청 바디와 헤더를 묶어서 엔티티로 설정
        HttpEntity<PaymentDTO> entity = new HttpEntity<>(paymentDTO, headers);

        // 4. 요청 보내기

        try {
            //key-in 방식을 쓰면 status가 done이므로 자동 결제 완료가 됨(confirm 구현 필요 없음)
            ResponseEntity<String> response = restClient.post(url, entity, String.class);

            //Toss 응답에서 데이터 추출
            JsonNode body = new ObjectMapper().readTree(response.getBody());
            String status = body.get("status").asText();

            if ("DONE".equals(status)) {
                PaymentResultDTO paymentResultDTO = PaymentResultDTO.builder()
                        .userId(userId) // or 로그인한 사용자 정보
                        .paymentKey(body.get("paymentKey").asText())
                        .orderId(body.get("orderId").asText())
                        .orderName(body.get("orderName").asText())
                        .amount(body.get("totalAmount").asInt())
                        .status(status)
                        .method(body.get("method").asText())
                        .paidAt(LocalDateTime.now()) // 또는 body.get("approvedAt").asText()
                        .build();

                savePaymentInfo(paymentResultDTO);
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {

            // Toss 응답이 없는 경우 대비하여 수동으로 status를 세팅
            PaymentResultDTO failDTO = PaymentResultDTO.builder()
                    .userId(userId)
                    .orderId(paymentDTO.getOrderId())
                    .orderName(paymentDTO.getOrderName())
                    .amount(paymentDTO.getAmount())
                    .status("FAIL") // ✅ 직접 세팅해서 NPE 방지
                    .method("카드") // Toss 응답 없이도 명시 가능
                    .paidAt(LocalDateTime.now())
                    .build();

            log.error("결제 실패 발생: {}", e.getResponseBodyAsString());
            throw e;
        }

        return ResponseEntity.ok("결제가 완료되었습니다.");
    }*/

    /*public ResponseEntity<String> confirmPayment(String paymentKey, String orderId, Double amount) {

        try {
            // Toss API URL
            String url = "https://api.tosspayments.com/v1/payments/confirm";

            String jsonBody = "{"
                    + "\"paymentKey\": \"" + paymentKey + "\","
                    + "\"amount\": " + amount + ","
                    + "\"orderId\": \"" + orderId + "\""
                    + "}";

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);


            // 정확한 인증 방식: secretKey 뒤에 ":" 붙이고 base64 인코딩
            String auth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
            headers.set("Authorization", "Basic " + auth);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            // POST 요청
            return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException e) {
            System.out.println("401에러 발생");
            System.out.println("status code : " + e.getStatusCode());
            System.out.println("response body : " + e.getResponseBodyAsString());
            throw e;
        }
    }*/

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
}
