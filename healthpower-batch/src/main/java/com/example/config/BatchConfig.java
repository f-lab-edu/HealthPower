package com.example.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class BatchConfig implements CommandLineRunner {

    private final JobLauncher jobLauncher;

    @Qualifier("expireCouponJob")
    private final Job expireCouponJob;

    @Qualifier("inactiveUserJob")
    private final Job inactivedUserJob;

    public BatchConfig(JobLauncher jobLauncher,
                       @Qualifier("expireCouponJob") Job expireCouponJob,
                       @Qualifier("inactiveUserJob") Job inactivedUserJob) {
        this.jobLauncher = jobLauncher;
        this.expireCouponJob = expireCouponJob;
        this.inactivedUserJob = inactivedUserJob;
    }

    @Override
    public void run(String... args) throws Exception {

        // 1. expireCouponJob은 runTime 파라미터를 요구 (예상)
        JobParameters expireParams = new JobParametersBuilder()
                .addLong("runTime", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(expireCouponJob, expireParams);
        System.out.println("✅ expireCouponJob 배치 실행 결과: " + execution.getStatus());

        // 2. inactiveUserJob은 cutoff 파라미터를 요구
        long cutoff = Instant.now()
                .minusSeconds(60L * 60 * 24 * 30) // 30일 전
                .toEpochMilli();

        JobParameters inactiveParams = new JobParametersBuilder()
                .addLong("cutoff", cutoff)
                .toJobParameters();

        JobExecution execution2 = jobLauncher.run(inactivedUserJob, inactiveParams);
        System.out.println("✅ inactivedUserJob 배치 실행 결과: " + execution2.getStatus());
    }
}