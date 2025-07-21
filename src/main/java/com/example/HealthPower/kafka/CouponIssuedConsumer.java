package com.example.HealthPower.kafka;

import com.example.HealthPower.entity.coupon.CouponIssuance;
import com.example.HealthPower.repository.CouponIssuanceRepository;
import com.example.HealthPower.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssuedConsumer {

    private final CouponIssuanceRepository issuanceRepository;
    private final CouponRepository couponRepository;

    @KafkaListener(topics = "coupon-issued", groupId = "coupon-consumer")
    @Transactional
    public void onMessage(CouponIssuedEvent e) {
        try {
            issuanceRepository.save(new CouponIssuance(e.couponId(), e.userId(), e.issuedAt()));
            couponRepository.decreaseStock(e.couponId());
            log.info("INSERT OK: coupon={}, user={}", e.couponId(), e.userId());
        } catch (DataIntegrityViolationException dup) {
            log.warn("중복 발급 무시: {}", dup.getMessage());
        } catch (Exception ex) {
            log.error("쿠폰 발급 실패", ex);
            throw ex;
        }
    }
}
