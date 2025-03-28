package com.example.HealthPower.util;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Optional;

//SLF4J(Simple Logging Facade for Java)
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    //SecurityContext에서 Authentication객체를 꺼내와서 이 객체를 통해 로그인한 username을 리턴해주는 간단한 유틸성 메소드
    public static Optional<String> getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            logger.debug("Security Context에 인증 정보가 없습니다");
            throw new RuntimeException("authentication 정보가 없음");
        }

        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        /*UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();*/
        //return authentication.getName();
        return Optional.ofNullable(username);
    }

}
