package com.example.HealthPower.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

//사용자 정보가 잘못되거나, 토큰이 유효하지 않은 경우에 대비하기 위한 클래스이다.(401 Unauthorized)
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.info("exception = {}", authException);
        authException.printStackTrace();
        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"로그인이 필요한 기능입니다.\"}");
    }
}
