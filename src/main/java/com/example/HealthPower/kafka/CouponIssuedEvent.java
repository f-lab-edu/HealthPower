package com.example.HealthPower.kafka;

import java.time.Instant;

public record CouponIssuedEvent(
        long couponId,
        String userId,
        Instant issuedAt
) {}
