package com.example.HealthPower.controller;

import com.example.HealthPower.dto.JoinDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller //@RestController
@RequiredArgsConstructor
@ResponseBody
public class JoinController {

    private final MemberService memberService;

    @PostMapping("/join")
    public String join(JoinDTO joinDTO, Model model) {

        try {
            memberService.join(joinDTO);
            return "home";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "join";
        }
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<User> getMyUserInfo() {
        return ResponseEntity.ok(memberService.getMyUserWithAuthorities().get());
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<User> getUserInfo(@PathVariable String userId) {
        return ResponseEntity.ok(memberService.getUserWithAuthorities(userId).get());
    }
}
