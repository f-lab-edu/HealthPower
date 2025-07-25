package com.example.listener;

import com.example.entity.User;
import com.example.entity.coupon.Coupon;
import com.example.entity.coupon.CouponIssuance;
import com.example.enumpackage.CouponStatus;
import com.example.infra.kafka.CouponIssuedEvent;
import com.example.repository.CouponIssuanceRepository;
import com.example.repository.CouponRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssuedEventListener {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final CouponIssuanceRepository couponIssuanceRepository;

    @Transactional
    @KafkaListener(topics = "coupon-issued", groupId = "coupon-group")
    public void handleCouponIssued(CouponIssuedEvent event) {
        log.info("✅ 쿠폰 발급 이벤트 수신: userId={}, couponId={}", event.userId(), event.couponId());

        try {


            User user = userRepository.findByUserId(event.userId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
            Coupon coupon = couponRepository.findById(event.couponId())
                    .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

            CouponIssuance issuance = CouponIssuance.builder()
                    .coupon(coupon)
                    .name(coupon.getName())
                    .user(user)
                    .issuedAt(event.issuedAt())
                    .expiredAt(Instant.now().plusSeconds(259200)) //3일
                    .status(CouponStatus.ACTIVE)
                    .isExpired(false)
                    .build();

            couponIssuanceRepository.save(issuance);

            log.info("🟢 쿠폰 발급 완료: {}", issuance);
        } catch (DataIntegrityViolationException e) {
            log.warn("중복 쿠폰 발급 시도 : userId = {}, couponId = {}", event.userId(), event.couponId());
        } catch (Exception e) {
            log.error("쿠폰 발급 처리 도중 에러 발생 = {}", e.getMessage());
            throw e;
        }
    }
}

