package com.example.controller.payment;

import com.example.dto.payment.PaymentRequestDTO;
import com.example.entity.iamport.OrderCreateRequest;
import com.example.entity.iamport.OrderCreateResponse;
import com.example.impl.UserDetailsImpl;
import com.example.repository.iamport.ImpPaymentRepository;
import com.example.service.ImpService;
import com.example.vo.IamportPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
//@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/impPayment")
public class ImpController {

    private final ImpService impService;
    private final ImpPaymentRepository impPaymentRepository;

    @PostMapping
    public ResponseEntity<OrderCreateResponse> createOrder(@RequestBody OrderCreateRequest request,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {

        //merchantUid 40자 내외로 설정
        String merchantUid = "order_" + userDetails.getUserId() + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);;

        log.info("🟢 Generated merchantUid: {}", merchantUid);
        log.info("🟢 Length: {}", merchantUid.length());

        OrderCreateResponse response = impService.createOrder(request, userDetails.getUserId(), merchantUid);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        // 1. 아임포트에서 액세스 토큰 발급
        String accessToken = impService.getToken();

        // 2. imp_uid로 결제내역 조회
        IamportPaymentResponse paymentInfo = impService.getPaymentInfo(paymentRequestDTO.getImpUid(), accessToken);

        // 4. 결제 성공 처리
        impService.savedPayment(paymentInfo);

        return ResponseEntity.ok("성공");
    }
}
