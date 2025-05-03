package com.example.HealthPower.controller.member;

import com.example.HealthPower.dto.user.UserDTO;
import com.example.HealthPower.dto.user.UserModifyDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.service.MemberService;
import com.example.HealthPower.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
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

    //마이페이지 사진 제대로 들어오는지 뷰로 확인
    @GetMapping("/myPage")
    public String myPage(Model model, Authentication authentication) {

        System.out.println("🔥 컨트롤러에서 authentication = " + authentication);
        System.out.println("🔥 principal = " + (authentication != null ? authentication.getPrincipal() : "null"));

        String userId = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("로그인 정보 없음"));

        User user = memberService.myInfo(userId)
                .orElseThrow(() -> new RuntimeException("회원 정보가 없습니다."));

        model.addAttribute("user", user);

        System.out.println("🧪 controller authentication: " + authentication);
        System.out.println("🧪 name: " + (authentication != null ? authentication.getName() : "null"));
        System.out.println("🧪 authorities: " + (authentication != null ? authentication.getAuthorities() : "null"));

        return "myPage";
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

