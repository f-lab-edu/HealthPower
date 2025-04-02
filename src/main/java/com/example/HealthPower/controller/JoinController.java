package com.example.HealthPower.controller;

import com.example.HealthPower.dto.JoinDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

//@Controller
@RestController
@RequiredArgsConstructor
@ResponseBody
@RequestMapping("/members")
public class JoinController {

    private final MemberService memberService;

    @PostMapping("/join")
    //@RequestBody를 통해 joinDTO에 바인딩
    public ResponseEntity join(@RequestBody @Valid JoinDTO joinDTO, BindingResult bindingResult) {

        /*if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }*/

        /*try {
            memberService.join(joinDTO);
            return "home";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "join";
        }*/

        return new ResponseEntity(memberService.join(joinDTO), HttpStatus.CREATED);
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
