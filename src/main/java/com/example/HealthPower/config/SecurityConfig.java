package com.example.HealthPower.config;

import com.example.HealthPower.handler.CustomAuthFailureHandler;
import com.example.HealthPower.handler.CustomAuthSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthSuccessHandler authSuccessHandler;
    private final CustomAuthFailureHandler authFailureHandler;

    private static final String[] AUTH_WHITELIST = {
            "/", "/login", "/signup"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        //csrf, cors, basichttp 비활성화
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        //세션 관리 구성
        http
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        //ForLogin, Logout 활성화
        http
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(authSuccessHandler)
                        .failureHandler(authFailureHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .logoutSuccessUrl("/login")
                );
        // permit, authenticated 경로 설정
        http
                .authorizeHttpRequests(authorize -> authorize
                        // 지정한 경로는 인증 없이 접근 허용
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        //나머지 모든 경로는 인증 필요
                        .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
