package com.example.HealthPower.controller.member;

import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class LogoutController {

    private final MemberService memberService;
    //private final RefreshTokenService refreshTokenService;

    /*@DeleteMapping("/logout")
    public ResponseEntity logout(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        refreshTokenService.deleteRefreshToken(refreshTokenDTO.getRefreshToken());
        return new ResponseEntity<>(HttpStatus.OK);
    }*/

    @PostMapping("/logout")
    public ResponseEntity logout(
            @RequestHeader("Authorization") String bearerToken,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        String accessToken = bearerToken.replace("Bearer ", "");
        String userId = userDetails.getUserId();

        memberService.logout(accessToken, userId);

        return ResponseEntity.ok("로그아웃 성공");
    }
}
