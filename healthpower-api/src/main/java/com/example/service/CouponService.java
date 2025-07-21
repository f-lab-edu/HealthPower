package com.example.service;

import com.example.entity.User;
import com.example.entity.coupon.Coupon;
import com.example.entity.coupon.CouponIssuance;
import com.example.enumpackage.CouponStatus;
import com.example.exception.coupon.*;
import com.example.impl.UserDetailsImpl;
import com.example.infra.kafka.CouponIssuedEvent;
import com.example.repository.CouponIssuanceRepository;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponIssuanceRepository couponIssuanceRepository;
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
                //expiredAt 세팅을 해줘야함.
                CouponIssuedEvent e = new CouponIssuedEvent(couponId, userId, Instant.now(), null);
                kafka.send("coupon-issued", userId, e);
                return res;
            }
        }
    }

    //쿠폰 목록 조회
    public List<CouponIssuance> getCoupons(User user) {
        return couponIssuanceRepository.findAllByUserAndStatus(user, CouponStatus.ACTIVE);
    }

    //쿠폰 발급 로직
    public CouponIssuance issueCoupon(Coupon coupon, User user) {
        //이미 발급된 쿠폰인지 확인
        if (couponIssuanceRepository.existsByCouponAndUser(coupon, user)) {
            throw new AlreadyIssuedException("이미 발급된 쿠폰입니다.");
        }

        //쿠폰 유효기간 체크
        if (coupon.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CouponExpiredException("이미 만료된 쿠폰입니다.");
        }

        CouponIssuance couponIssuance = CouponIssuance.builder()
                .coupon(coupon)
                .user(user)
                .issuedAt(Instant.now())
                .expiredAt(LocalDateTime.now().plusDays(3))
                .status(CouponStatus.ACTIVE)
                .isExpired(false)
                .build();

        return couponIssuanceRepository.save(couponIssuance);
    }

    //쿠폰 사용 로직
    public void useCoupon(CouponIssuance couponIssuance, int orderPrice) {

        //사용 여부 검사
        if (couponIssuance.getStatus() == CouponStatus.USED) {
            throw new AlreadyUsedCouponException("이미 사용한 쿠폰입니다.");
        }

        //유효기간 검사
        if (couponIssuance.getCoupon().getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CouponExpiredException("이미 만료된 쿠폰입니다.");
        }

        //최소 주문 금액 체크
        if (orderPrice < couponIssuance.getCoupon().getAmount()) {
            throw new CouponMinException("최소 금액 주문 미달입니다.");
        }

        //사용처리
        couponIssuance.setStatus(CouponStatus.USED);
        couponIssuance.setUsedAt(LocalDateTime.now());
    }

    //할인 금액 계산
    public int calculateDiscountAmount(Coupon coupon) {
        return coupon.getAmount();
    }
}
