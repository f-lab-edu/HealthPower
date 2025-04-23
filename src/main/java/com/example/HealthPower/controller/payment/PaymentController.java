package com.example.HealthPower.controller.payment;

import com.example.HealthPower.dto.payment.PaymentConfirmDTO;
import com.example.HealthPower.dto.payment.PaymentDTO;
import com.example.HealthPower.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    //결제 요청
    @PostMapping("/request")
    public ResponseEntity<?> requestPayment(@RequestBody PaymentDTO paymentDTO) {
        return paymentService.requestPayment(paymentDTO);
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmDTO paymentConfirmDTO) {
        String paymentKey = paymentConfirmDTO.getPaymentKey();
        String orderId = paymentConfirmDTO.getOrderId();
        Double amount = paymentConfirmDTO.getAmount();

        ResponseEntity<String> response = paymentService.confirmPayment(paymentKey, orderId, amount);

        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok("결제 승인 완료");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 승인 실패");
        }
    }

    @PostMapping("/success")

    public ResponseEntity<?> paymentSuccess(@RequestParam String paymentKey,
                                            @RequestParam String orderId,
                                            @RequestParam Long amount) {
        // 이 값을 바탕으로 토스에 최종 결제 승인 요청하고
        // DB에 결제 내역 저장하면 됨
        return ResponseEntity.ok("결제 성공!");
    }

    @PostMapping("/fail")
    public ResponseEntity<?> paymentFail(@RequestParam String code,
                                         @RequestParam String message,
                                         @RequestParam String orderId) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 실패 : " + message);
    }
}
