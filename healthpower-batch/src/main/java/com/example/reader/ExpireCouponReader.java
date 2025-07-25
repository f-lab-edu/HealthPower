package com.example.reader;

import com.example.entity.User;
import com.example.entity.coupon.CouponIssuance;
import com.example.enumpackage.CouponStatus;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

//만료 대상 쿠폰만 읽기
@Slf4j
@Configuration
public class ExpireCouponReader {
    @Bean
    @StepScope
    public JpaPagingItemReader<CouponIssuance> couponReader(EntityManagerFactory entityManagerFactory,
                                                            @Value("#{jobParameters['runTime']}") Long runTime) {

        LocalDateTime now = Instant.ofEpochMilli(runTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        JpaPagingItemReader<CouponIssuance> reader = new JpaPagingItemReader<>();

        reader.setName("couponReader");
        reader.setQueryString("SELECT c FROM CouponIssuance c WHERE c.status = :status AND c.expiredAt < :now");
        reader.setParameterValues(Map.of("status", CouponStatus.ACTIVE, "now", now));
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setPageSize(100); // chunk 크기와 맞추기
        reader.setSaveState(false); // 단순 스케줄러 실행이므로 false로 둬도 됨
        return reader;
    }
}
