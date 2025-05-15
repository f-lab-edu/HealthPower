package com.example.HealthPower.controller.member;

import com.example.HealthPower.dto.login.JoinDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.repository.UserRepository;
import com.example.HealthPower.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//이미지 업로드를 위한 form-data 형식으로 회원가입 controller
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class JoinController2 {

    private final MemberService memberService;
    private final UserRepository userRepository;

    // 회원가입 폼
    @GetMapping("/join")
    public String showJoinForm(Model model) {
        model.addAttribute("joinDTO", new JoinDTO());
        return "join";
    }

    // form-data 방식으로 회원가입 + 사진 업로드
    @PostMapping(
            value = "/join",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String join(
            @Validated @ModelAttribute JoinDTO joinDTO,
            BindingResult bindingResult
    ) {

        if (userRepository.existsByUserId(joinDTO.getUserId())) {
            bindingResult.rejectValue("userId", "duplicate", "이미 사용 중인 아이디입니다.");
            return "join";
        }

        if (userRepository.existsByNickname(joinDTO.getNickname())) {
            bindingResult.rejectValue("userNickname", "duplicate", "이미 사용 중인 닉네임입니다.");
        }

        if (userRepository.existsByEmail(joinDTO.getEmail())) {
            bindingResult.rejectValue("userEmail", "duplicate", "이미 사용 중인 이메일입니다.");
        }

        if (bindingResult.hasErrors()) {
            return "join";
        }

        memberService.join(joinDTO);

        return "redirect:/members/joinSuccess";
    }

    @GetMapping("/joinSuccess")
    public String joinSuccess() {
        return "joinSuccess";
    }

    @GetMapping("/checkUserId")
    public ResponseEntity<Boolean> checkUserId(@RequestParam("userId") String userId) {
        return ResponseEntity.ok(userRepository.existsByUserId(userId));
    }

    @GetMapping("/checkUserNickname")
    public ResponseEntity<Boolean> checkUserNickname(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok(userRepository.existsByNickname(nickname));
    }

    @GetMapping("/checkUserEmail")
    public ResponseEntity<Boolean> checkUserEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(userRepository.existsByEmail(email));
    }
}
