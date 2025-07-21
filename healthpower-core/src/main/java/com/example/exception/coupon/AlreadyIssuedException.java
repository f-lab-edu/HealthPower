package com.example.exception.coupon;

public class AlreadyIssuedException extends RuntimeException {
    public AlreadyIssuedException(String message) {
        super(message);
    }
}
