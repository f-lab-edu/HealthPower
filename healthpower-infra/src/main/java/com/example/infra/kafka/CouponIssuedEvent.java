package com.example.infra.kafka;

import java.time.Instant;
import java.time.LocalDateTime;

public record CouponIssuedEvent(
        long couponId,
        String userId,
        Instant issuedAt,
        LocalDateTime expiredAt
) {}
