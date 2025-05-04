package com.example.HealthPower.controller.payment;

import com.example.HealthPower.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

//@RestController
@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

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
}
