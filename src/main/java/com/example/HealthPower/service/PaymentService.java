package com.example.HealthPower.service;

import com.example.HealthPower.dto.payment.PaymentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

//토스 결제 요청
@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${toss.secret}")
    private String secretKey;

    private final RestTemplate restTemplate;

    public ResponseEntity<String> requestPayment(PaymentDTO paymentDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(secretKey, ""); //Basic 인증
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PaymentDTO> entity = new HttpEntity<>(paymentDTO, headers);

        String url = "https://api.tosspayments.com/v1/payments/key-in"; //백엔드로만 구현해야하기 때문에 key-in 방식으로 구현
        return restTemplate.postForEntity(url, entity, String.class);
    }

    public ResponseEntity<String> confirmPayment(String paymentKey, String orderId, Double amount) {

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
        headers.set("Authorization",
                Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8)));  // 기본 인증
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        // POST 요청
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

}
