package com.example.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InActivatedUserScheduler {

    private final JobLauncher jobLauncher;
    private final Job inActivatedUserJob;

    public InActivatedUserScheduler(JobLauncher jobLauncher, @Qualifier("inactiveUserJob") Job inActivatedUserJob) {
        this.jobLauncher = jobLauncher;
        this.inActivatedUserJob = inActivatedUserJob;
    }

    @Scheduled(cron = "0 0 3 * * SUN") // 매주 일요일 새벽 3시
    public void runDeactivateUserJob() throws Exception {

        log.info("🕐 [DeactivateUserScheduler] 사용자 비활성화 Job 시작");

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("cutoff", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(inActivatedUserJob, jobParameters);
    }
}
