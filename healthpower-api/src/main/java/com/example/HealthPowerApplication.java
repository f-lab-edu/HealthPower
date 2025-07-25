package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing //auditing 기능을 위해 추가
@SpringBootApplication(scanBasePackages = "com.example") //전체 패키지 스캔
@EnableCaching // 레디스 캐싱을 위해 추가, @Cacheable 같은 어노테이션을 인식 하게 함
public class HealthPowerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthPowerApplication.class, args);
	}

}
