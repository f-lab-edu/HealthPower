package com.example.infra.kafka;

import java.time.Instant;

public record CouponIssuedEvent(
        long couponId,
        String userId,
        Instant issuedAt,
        Instant expiredAt
) {}
