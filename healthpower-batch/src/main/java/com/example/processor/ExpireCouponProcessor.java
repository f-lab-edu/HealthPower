package com.example.processor;

import com.example.entity.coupon.CouponIssuance;
import com.example.entity.log.ExpiredCouponLog;
import com.example.enumpackage.CouponStatus;
import com.example.repository.ExpiredCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//읽은 쿠폰을 만료 처리
@Component
@Slf4j
@RequiredArgsConstructor
public class ExpireCouponProcessor implements ItemProcessor<CouponIssuance, CouponIssuance> {

    private final ExpiredCouponRepository expiredCouponRepository;

    @Override
    public CouponIssuance process(CouponIssuance couponIssuance) throws Exception {
        log.info("🟢 Processor 진입: id={}, expiredAt={}, isExpire()={}",
                couponIssuance.getId(),
                couponIssuance.getExpiredAt(),
                couponIssuance.isExpired()
        );

        // 만료 대상이면 expire() 호출 → 상태 변경
        if (couponIssuance.isExpired()) {
            couponIssuance.expire();

            ExpiredCouponLog log = new ExpiredCouponLog();

            log.setReason("만료된 쿠폰");
            log.setName(couponIssuance.getName());
            log.setExpiredAt(LocalDateTime.now());

            expiredCouponRepository.save(log);

            return couponIssuance; // writer로 전달
        }
        return null; // writer로 전달되지 않음
    }
}