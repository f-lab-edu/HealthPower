package com.example.HealthPower.jwt;

import com.example.HealthPower.service.BlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    //토스sdk api 인증 요청 예외 처리
    /*private static final List<AntPathRequestMatcher> excludeUrlPatterns = List.of(
            new AntPathRequestMatcher("/payment/success"),
            new AntPathRequestMatcher("/payment/fail")
    );*/

    @Qualifier("redisTemplate")
    private final RedisTemplate<String, String> redisTemplate;

    /*@Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // "/payment/success", "/payment/fail" 요청은 이 필터를 거치지 않게 설정
        String path = request.getRequestURI();
        return path.startsWith("/payment/success") || path.startsWith("/payment/fail");
    }*/

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //1. Request Header 에서 토큰을 꺼냄
        String token = resolveToken((HttpServletRequest) request);

        // 2. validateToken 으로 토큰 유효성 검사
        // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // 👉 블랙리스트(로그아웃된 토큰) 체크
            Boolean isBlackListed = redisTemplate.hasKey("blackList : " + token);
            if (isBlackListed) {
                throw new RuntimeException("로그아웃 혹은 탈퇴한 사용자입니다.");
            }

            // 👉 블랙리스트 아니면 정상 인증 처리
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }


    // 실제 필터링 로직은 doFilterInternal 에 들어감
    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할 수행
    /*public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        //1. Request Header 에서 토큰을 꺼냄
        String token = resolveToken((HttpServletRequest) request);

        // 2. validateToken 으로 토큰 유효성 검사
        // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // 👉 블랙리스트(로그아웃된 토큰) 체크
            Boolean isBlackListed = redisTemplate.hasKey("blackList : " + token);
            if (isBlackListed) {
                throw new RuntimeException("로그아웃 혹은 탈퇴한 사용자입니다.");
            }

            // 👉 블랙리스트 아니면 정상 인증 처리
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);

    }*/

    // Request Header에서 Token 정보 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); //"Bearer "제거
        }
        return null;
    }
}
