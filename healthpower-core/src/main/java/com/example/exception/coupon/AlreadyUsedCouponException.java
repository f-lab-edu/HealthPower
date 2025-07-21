package com.example.exception.coupon;

public class AlreadyUsedCouponException extends RuntimeException {
    public AlreadyUsedCouponException(String message) {
        super(message);
    }
}
