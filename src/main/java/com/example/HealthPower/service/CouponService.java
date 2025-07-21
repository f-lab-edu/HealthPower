package com.example.HealthPower.service;

import com.example.HealthPower.exception.coupon.DuplicateClaimException;
import com.example.HealthPower.exception.coupon.SoldOutException;
import com.example.HealthPower.kafka.CouponIssuedEvent;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final StringRedisTemplate redis;
    private final RedisScript<Long> claimScript;
    private final KafkaTemplate<String, CouponIssuedEvent> kafka;

    @Transactional
    public long claim(long couponId, String userId) {
        List<String> keys = List.of(
                "coupon:stock:" + couponId,
                "coupon:claimed:" + couponId
        );

        Long res = redis.execute(claimScript, keys, userId);
        Assert.notNull(res, "Redis 정상 작동");

        switch (res.intValue()) {
            case -1 -> throw new SoldOutException();
            case -2 -> throw new DuplicateClaimException();
            default -> {
                CouponIssuedEvent e = new CouponIssuedEvent(couponId, userId, Instant.now());
                kafka.send("coupon-issued", userId, e);
                return res;
            }
        }
    }
}
