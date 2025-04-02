package com.example.HealthPower.controller;

import com.example.HealthPower.dto.RefreshTokenDTO;
import com.example.HealthPower.service.MemberService;
import com.example.HealthPower.service.RefreshTokenService;
import com.example.HealthPower.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class LogoutController {

    private final RefreshTokenService refreshTokenService;

    @DeleteMapping("/logout")
    public ResponseEntity logout(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        refreshTokenService.deleteRefreshToken(refreshTokenDTO.getRefreshToken());
        System.out.println("로그아웃 되었습니다.");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
