package com.example.HealthPower.controller.payment;

import com.example.HealthPower.entity.User;
import com.example.HealthPower.entity.payment.Payment;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

//@RestController
@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    //결제 요청
    @GetMapping("/productDetail")
    public String productDetail() {
        return "productDetail";
    }

    @GetMapping
    public String paymentPage() {
        return "payment";
    }

    @PostMapping("/request")
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal UserDetailsImpl user,
                                         @RequestBody Payment payment) {
        String orderId = "order_" + UUID.randomUUID(); // or Toss 요구 포맷

        paymentService.savePaymentInfoRedis(orderId, user.getUserId(), payment.getQuantity(), payment.getOrderName(), payment.getProductId());

        // Toss 결제 페이지로 redirect URL 생성
        return ResponseEntity.ok(Map.of(
                "userId", user.getUserId(),
                "orderId", orderId,
                "orderName", payment.getOrderName(),
                "amount", payment.getAmount(),
                "redirectUrl", "http://localhost:8080/payment/success" // Toss SDK URL
        ));
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("paymentKey") String paymentKey,
                                 @RequestParam("orderId") String orderId,
                                 @RequestParam("amount") Long amount,
                                 @AuthenticationPrincipal UserDetailsImpl user,
                                 RedirectAttributes redirectAttributes) {
        // 이 값을 바탕으로 토스에 최종 결제 승인 요청하고
        // DB에 결제 내역 저장하면 됨
        try {
            paymentService.approvePayment(paymentKey, orderId, amount, user.getUserId());
            return "paymentSuccess";
        } catch (IllegalArgumentException e) {
            // Redis에서 주문 정보 꺼냄
            Map<String, Object> orderInfo = paymentService.getOrderInfoRedis(orderId);
            String productId = orderInfo.get("productId").toString();

            // 실패 메시지 전달
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            return "redirect:/board/product/" + productId;
        }

    }

    @GetMapping("/fail")
    public String paymentFail(@RequestParam("orderId") String orderId,
                              @RequestParam(value = "message", required = false) String message,
                              @RequestParam(value = "fail", required = false) String fail,
                              RedirectAttributes redirectAttributes) {

        // Redis에 있는 주문 정보에서 상품 ID를 가져온다
        Map<String, Object> orderInfo = paymentService.getOrderInfoRedis(orderId);
        String productId = orderInfo.get("productId").toString(); // 미리 Redis에 저장해둬야 함

        // 메시지를 전달 (잔액 부족 등)
        redirectAttributes.addFlashAttribute("errorMessage", message);

        return "redirect:/board/product/" + productId;  // 상세 페이지로 복귀
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
