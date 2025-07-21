package com.example.config;

import org.springframework.web.filter.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

//프론트엔드, 백엔드를 구분지어서 개발하거나, 서로 다른 Server 환경에서 자원을 공유할 때,
//Cors설정이 안 되어있으면 오류가 발생한다. 이를 방지하기 위해 Cors 설정을 해주어야 한다.
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }

}
