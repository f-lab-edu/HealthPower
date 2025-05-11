package com.example.HealthPower.config;

import com.example.HealthPower.jwt.JwtAccessDeniedHandler;
import com.example.HealthPower.jwt.JwtAuthenticationEntryPoint;
import com.example.HealthPower.jwt.JwtAuthenticationFilter;
import com.example.HealthPower.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtTokenProvider jwtTokenProvider;
    private final CorsFilter corsFilter;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    //BcryptPasswordEncoder는 위의 PasswordEncoder의 구현 클래스이며, Bcrypt 해시 함수를 사용해 비밀번호를 암호화한다.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
        //csrf, cors, basichttp 비활성화
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/chat/exit/**"))
                .csrf(AbstractHttpConfigurer::disable)
                //.cors(AbstractHttpConfigurer::disable) form-data 테스트를 위해서 주석처리
                .cors(cors -> {})
                //.formLogin(AbstractHttpConfigurer::disable) //form 로그인 테스트용
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                // permit, authenticated 경로 설정
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        // 지정한 경로는 인증 없이 접근 허용
                        .requestMatchers("/members/join-success").permitAll()
                        //.requestMatchers("/members/login2").permitAll() //form login 테스트용
                        //.requestMatchers("/chat/exit/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll() //정적자원 허용
                        .requestMatchers("/", "/members/login", "/members/join", "/test", "/payment/**").permitAll()
                        //나머지 모든 경로는 인증 필요
                        .anyRequest().authenticated()
                )
                //세션 관리 구성(세션을 사용하지 않기 때문에 STATELESS로 설정)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }

}
