package com.example.HealthPower.controller;

import com.example.HealthPower.dto.UserDTO;
import com.example.HealthPower.dto.UserModifyDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;

    /* 마이페이지 보기 */
    @GetMapping("/myInfo")
    public Optional<User> searchMyInfo(UserDTO userDTO) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        String userId = userDetails.getUserId();

        Optional<User> infoResult = memberService.myInfo(userId);

        return infoResult;
    }

    /* 마이페이지 수정 */
    @PutMapping("/myInfoUpdate")
    public ResponseEntity<String> saveMyInfo(@RequestBody UserModifyDTO userModifyDTO) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        String userId = userDetails.getUserId();

        memberService.myInfoUpdate(userModifyDTO);

        return ResponseEntity.ok("회원 정보가 성공적으로 업데이트되었습니다.");
    }
}

