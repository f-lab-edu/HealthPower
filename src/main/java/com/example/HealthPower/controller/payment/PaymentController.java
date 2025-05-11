package com.example.HealthPower.controller.payment;

import com.example.HealthPower.entity.User;
import com.example.HealthPower.entity.payment.TransactionHistory;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.repository.TransactionHistoryRepository;
import com.example.HealthPower.repository.UserRepository;
import com.example.HealthPower.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    //결제 요청
    @GetMapping
    public String paymentPage() {
        return "payment";
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("paymentKey") String paymentKey,
                                 @RequestParam("orderId") String orderId,
                                 @RequestParam("amount") Long amount,
                                 @RequestParam("userId") String userId,
                                 @RequestParam("orderName") String orderName) {
        // 이 값을 바탕으로 토스에 최종 결제 승인 요청하고
        // DB에 결제 내역 저장하면 됨
        paymentService.approvePayment(paymentKey, orderId, amount, orderName, userId);

        return "paymentSuccess";
    }

    @GetMapping("/fail")
    public String paymentFail(@RequestParam("code") String code,
                              @RequestParam("message") String message,
                              @RequestParam("orderId") String orderId,
                              @RequestParam(value = "userId", required = false) String userId,
                              @RequestParam(value = "orderName", required = false) String orderName) {

        paymentService.saveFailedPayment(code, message, orderId, orderName, userId);
        return "paymentFail";
    }

    @PostMapping("/charge")
    public ResponseEntity<String> chargeBalance(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @RequestParam int amount) {
        User user = userRepository.findByUserId(userDetails.getUserId()).orElseThrow();
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
        return ResponseEntity.ok("충전 완료. 현재 잔액: " + user.getBalance());
    }

    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findByUserId(userDetails.getUserId()).orElseThrow();
        return ResponseEntity.ok(user.getBalance());
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionHistory>> getHistory(@AuthenticationPrincipal UserDetailsImpl user) {
        List<TransactionHistory> histories = transactionHistoryRepository.findByUserId(user.getUserId());
        return ResponseEntity.ok(histories);
    }

}
