package com.example.HealthPower.controller.member;

import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.jwt.JwtTokenProvider;
import com.example.HealthPower.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
//@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Controller
public class LogoutController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;

    @PostMapping("/logout")
    public ResponseEntity logout(
            @RequestHeader("Authorization") String bearerToken,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        String accessToken = bearerToken.replace("Bearer ", "");
        String userId = userDetails.getUserId();

        memberService.logout(accessToken, userId);

        return ResponseEntity.ok("로그아웃 성공");
    }

    @PostMapping("/logout2")
    public String logout2(@CookieValue(name = "Authorization", required = false) String accessToken,
                          @AuthenticationPrincipal UserDetailsImpl userDetails,
                          HttpServletResponse response) {
        try {

            if (accessToken != null && userDetails != null) {
                long expiration = jwtTokenProvider.getExpiration(accessToken);
                redisTemplate.opsForValue().set("blackList : " + accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

                String userId = userDetails.getUserId();
                redisTemplate.delete("refreshToken : " + userId);
            }

            Cookie expiredCookie = new Cookie("Authorization", null);
            expiredCookie.setPath("/");
            expiredCookie.setMaxAge(0);
            response.addCookie(expiredCookie);

            SecurityContextHolder.clearContext();

            return "redirect:/members/login";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/members/menu";
        }
    }
}
