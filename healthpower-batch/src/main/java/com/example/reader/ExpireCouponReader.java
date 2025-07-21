package com.example.reader;

import com.example.entity.coupon.CouponIssuance;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//만료 대상 쿠폰만 읽기
@Configuration
public class ExpireCouponReader {
    @Bean
    @StepScope
    public JpaPagingItemReader<CouponIssuance> couponReader(EntityManagerFactory entityManagerFactory) {
        JpaPagingItemReader<CouponIssuance> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT c FROM CouponIssuance c WHERE c.status = 'ACTIVE' AND c.expiredAt < CURRENT_TIMESTAMP");
        reader.setPageSize(100); // chunk 크기와 맞추기
        reader.setSaveState(false); // 단순 스케줄러 실행이므로 false로 둬도 됨
        return reader;
    }
}
