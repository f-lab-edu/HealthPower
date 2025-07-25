package com.example.processor;

import com.example.entity.coupon.CouponIssuance;
import com.example.enumpackage.CouponStatus;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

//읽은 쿠폰을 만료 처리
@Component
public class ExpireCouponProcessor implements ItemProcessor<CouponIssuance, CouponIssuance> {
    @Bean
    public ItemProcessor<CouponIssuance, CouponIssuance> couponProcessor() {
        return coupon -> {
            coupon.markAsExpired(); // status → EXPIRED
            return coupon;
        };
    }

    @Override
    public CouponIssuance process(CouponIssuance couponIssuance) throws Exception {
        // 만료 대상이면 expire() 호출 → 상태 변경
        if (couponIssuance.isExpire()) {
            couponIssuance.expire();
            return couponIssuance; // writer로 전달
        }
        return null; // writer로 전달되지 않음
    }
}