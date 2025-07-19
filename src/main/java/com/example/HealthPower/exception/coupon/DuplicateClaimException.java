package com.example.HealthPower.exception.coupon;

public class DuplicateClaimException extends RuntimeException {
    public DuplicateClaimException() {
        super("이미 발급된 쿠폰입니다.");
    }
}
