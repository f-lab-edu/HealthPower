package com.example.writer;

import com.example.entity.coupon.CouponIssuance;
import com.example.repository.CouponIssuanceRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//변경된 상태를 DB에 반영
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ExpireCouponWriter implements ItemWriter<CouponIssuance> {

    private final CouponIssuanceRepository couponIssuanceRepository;

    @Bean
    public JpaItemWriter<CouponIssuance> couponWriter(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<CouponIssuance> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Override
    public void write(Chunk<? extends CouponIssuance> items) throws Exception {
        // 상태가 이미 변경된 엔티티를 saveAll()로 저장
        log.info("🔄 DB 반영할 만료 쿠폰 수: {}", items.size());
        for (CouponIssuance item : items) {
            log.info("⛔ 만료 대상 ID: {}, 상태: {}", item.getId(), item.getStatus());
        }
        couponIssuanceRepository.saveAll(items);
    }
}