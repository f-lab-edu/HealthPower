package com.example.job;

import com.example.entity.coupon.CouponIssuance;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ExpireCouponJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job expireCouponJob(Step expireCouponStep) {
        return new JobBuilder("expireCouponJob", jobRepository)
                .start(expireCouponStep)
                .build();
    }

    @Bean
    public Step expireCouponStep(JobRepository jobRepository,
                                 PlatformTransactionManager txManager,
                                 JpaPagingItemReader<CouponIssuance> couponReader,
                                 ItemProcessor<CouponIssuance, CouponIssuance> couponProcessor,
                                 JpaItemWriter<CouponIssuance> couponWriter) {
        return new StepBuilder("expireCouponStep", jobRepository)
                .<CouponIssuance, CouponIssuance>chunk(100, transactionManager)
                .reader(couponReader)
                .processor(couponProcessor)
                .writer(couponWriter)
                .build();
    }

}
