package com.example;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchJobRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job expireCouponJob;

    @Override
    public void run(String... args) throws Exception {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("runTime", System.currentTimeMillis()) // 파라미터 다르게 해야 중복 실행 허용
                .toJobParameters();

        JobExecution execution = jobLauncher.run(expireCouponJob, parameters);
        System.out.println("✅ 배치 실행 결과: " + execution.getStatus());
    }
}
