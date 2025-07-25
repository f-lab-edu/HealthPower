package com.example.job;

import com.example.entity.User;
import com.example.processor.InActivatedUserProcessor;
import com.example.writer.InActivatedUserWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class InActivatedUserJobConfig {

    private final InActivatedUserProcessor processor;

    @Bean
    public Job inactiveUserJob(JobRepository jobRepository, Step inactiveUserStep) {
        return new JobBuilder("inactiveUserJob", jobRepository)
                .start(inactiveUserStep)
                .build();
    }

    @Bean
    public Step inactiveUserStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 JpaPagingItemReader<User> inactiveUserReader,
                                 InActivatedUserWriter inactiveUserWriter) {
        return new StepBuilder("inactiveUserStep", jobRepository)
                .<User, User>chunk(100, transactionManager)
                .reader(inactiveUserReader)
                .processor(processor)
                .writer(inactiveUserWriter)
                .build();
    }
}

