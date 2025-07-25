package com.example.exception.coupon;

public class SoldOutException extends RuntimeException {
    public SoldOutException() {
        super("쿠폰 재고가 없습니다.");
    }
}
