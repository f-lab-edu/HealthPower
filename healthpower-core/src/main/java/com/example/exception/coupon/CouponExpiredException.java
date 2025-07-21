package com.example.exception.coupon;

public class CouponExpiredException extends RuntimeException {
    public CouponExpiredException(String message) {
        super(message);
    }
}
