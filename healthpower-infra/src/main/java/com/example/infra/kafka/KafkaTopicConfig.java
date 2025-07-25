package com.example.infra.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean // ✅ Spring이 이 메서드를 실행해서 NewTopic을 Kafka에 생성
    public NewTopic couponExpiredTopic() {
        return TopicBuilder.name("coupon-expired")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic couponIssuedTopic() {
        return TopicBuilder.name("coupon-issued")
                .partitions(3)
                .replicas(1)
                .build();
    }
}