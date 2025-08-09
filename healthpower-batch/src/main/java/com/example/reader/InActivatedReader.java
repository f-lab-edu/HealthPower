package com.example.reader;

import com.example.entity.User;
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

@Slf4j
@Configuration
public class InActivatedReader {
    @StepScope
    @Bean
    public JpaPagingItemReader<User> InActivatedUserReader(EntityManagerFactory entityManagerFactory,
                                                           @Value("#{jobParameters['cutoff'] ?: T(java.lang.System).currentTimeMillis()}") Long cutoffMillis) {
        LocalDateTime jobStartTime = Instant.ofEpochMilli(cutoffMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime cutoffDate = jobStartTime.minusMonths(3);

        log.info("⏱️ jobStartTime 기준 시각: {}", jobStartTime);
        log.info("⏱️ cutoff 기준 시각: {}", cutoffDate);

        return new JpaPagingItemReaderBuilder<User>()
                .name("userReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT u FROM User u WHERE u.lastLoginAt < :cutoffDate AND u.activated = true")
                .parameterValues(Map.of("cutoffDate", cutoffDate))
                .pageSize(100)
                .saveState(false)
                .build();
    }
}
