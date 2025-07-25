package com.example;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BatchJobRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;

    private final Job expireCouponJob;

    private final Job inactivedUserJob;

    public BatchJobRunner(JobLauncher jobLauncher,
                          @Qualifier("expireCouponJob")Job expireCouponJob,
                          @Qualifier("inactiveUserJob") Job inactivedUserJob) {
        this.jobLauncher = jobLauncher;
        this.expireCouponJob = expireCouponJob;
        this.inactivedUserJob = inactivedUserJob;
    }

    @Override
    public void run(String... args) throws Exception {
        JobParameters parameters = new JobParametersBuilder()
                /*.addLong("runTime", System.currentTimeMillis()) // 파라미터 다르게 해야 중복 실행 허용*/
                .addLong("cutoff", System.currentTimeMillis()) // 파라미터 다르게 해야 중복 실행 허용
                .toJobParameters();

        JobExecution execution = jobLauncher.run(expireCouponJob, parameters);
        System.out.println("✅ expireCouponJob 배치 실행 결과: " + execution.getStatus());

        JobExecution execution2 = jobLauncher.run(inactivedUserJob, parameters);
        System.out.println("✅ inactivedUserJob 배치 실행 결과: " + execution2.getStatus());
    }
}
