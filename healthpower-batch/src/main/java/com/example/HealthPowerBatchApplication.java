package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan(basePackages = "com.example.entity")
@EnableScheduling
public class HealthPowerBatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthPowerBatchApplication.class, args);
    }
}
