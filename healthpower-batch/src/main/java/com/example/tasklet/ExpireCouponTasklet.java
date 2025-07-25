package com.example.tasklet;

import com.example.entity.coupon.Coupon;
import com.example.entity.coupon.CouponIssuance;
import com.example.repository.CouponIssuanceRepository;
import com.example.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExpireCouponTasklet implements Tasklet {

    private final CouponIssuanceRepository couponIssuanceRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        List<CouponIssuance> coupons = couponIssuanceRepository.findAllByExpiredAtBeforeAndIsExpiredFalse(LocalDateTime.now());

        for (CouponIssuance couponIssuance : coupons) {
            couponIssuance.setExpired(true);
        }

        couponIssuanceRepository.saveAll(coupons);

        return RepeatStatus.FINISHED;
    }
}
