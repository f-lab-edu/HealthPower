package com.example.HealthPower.controller.coupon;

import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
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
        return ResponseEntity.ok(Map.of("left", left));

    }
}

