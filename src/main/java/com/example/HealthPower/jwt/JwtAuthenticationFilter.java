package com.example.HealthPower.jwt;

import com.example.HealthPower.service.BlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Qualifier("redisTemplate")
    private final RedisTemplate<String, String> redisTemplate;
    private final BlackListService blackListService;

    // 실제 필터링 로직은 doFilterInternal 에 들어감
    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할 수행
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

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

        chain.doFilter(request,response);

}

// Request Header에서 Token 정보 추출
public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {

        System.out.println("확인용 토큰: " + bearerToken.substring(7));
        return bearerToken.substring(7); //"Bearer "제거

    }

    return null;
}
}
