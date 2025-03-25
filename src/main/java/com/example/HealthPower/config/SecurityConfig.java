package com.example.HealthPower.config;

import com.example.HealthPower.handler.CustomAuthFailureHandler;
import com.example.HealthPower.handler.CustomAuthSuccessHandler;
//import com.example.HealthPower.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //private final JwtTokenProvider jwtTokenProvider;

    private final CustomAuthSuccessHandler authSuccessHandler;
    private final CustomAuthFailureHandler authFailureHandler;

    private static final String[] AUTH_WHITELIST = {
            "/", "/login", "/join", "/h2-console/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //csrf, cors, basichttp 비활성화
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        //세션 관리 구성
        http
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        // permit, authenticated 경로 설정
        http
                .authorizeHttpRequests(authorize -> authorize
                        // 지정한 경로는 인증 없이 접근 허용
                        .requestMatchers(AUTH_WHITELIST)
                        .permitAll()
                        //나머지 모든 경로는 인증 필요
                        .anyRequest().authenticated());

        return http.build();
    }

    //BcryptPasswordEncoder는 위의 PasswordEncoder의 구현 클래스이며, Bcrypt 해시 함수를 사용해 비밀번호를 암호화한다.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
