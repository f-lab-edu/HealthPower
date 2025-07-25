package com.example.controller.coupon;

import com.example.impl.UserDetailsImpl;
import com.example.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
//@Controller
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @PostMapping("/{id}/claim")
    public ResponseEntity<?> claim(@PathVariable long id, @AuthenticationPrincipal UserDetailsImpl user) {
        long left = couponService.claim(id, user.getUserId());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "left", left,
                "message", "쿠폰이 발급되었습니다."));
    }
}

