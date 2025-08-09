package com.example;

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

        long currentMillis = System.currentTimeMillis();

        // expireCouponJob을 위한 파라미터 (runTime 사용)
        JobParameters expireCouponJobParameters = new JobParametersBuilder()
                .addLong("runTime", currentMillis)
                .addString("jobId","expireCouponJob_" + currentMillis) //각 Job 실행을 고유하게 식별
                .toJobParameters();

        // inactiveUserJob을 위한 파라미터 (cutoff 사용)
        JobParameters inactivedUserJobParameters = new JobParametersBuilder()
                .addLong("cutoff", currentMillis)
                .addString("jobId", "inactivedUserJob_" + currentMillis) // 각 잡 실행을 고유하게 식별
                .toJobParameters();

        JobExecution execution1 = jobLauncher.run(expireCouponJob, expireCouponJobParameters);
        System.out.println("✅ expireCouponJob 배치 실행 결과: " + execution1.getStatus());

        JobExecution execution2 = jobLauncher.run(inactivedUserJob, inactivedUserJobParameters);
        System.out.println("✅ inactivedUserJob 배치 실행 결과: " + execution2.getStatus());
    }
}
