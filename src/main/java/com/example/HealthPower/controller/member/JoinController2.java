package com.example.HealthPower.controller.member;

import com.example.HealthPower.dto.login.JoinDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//이미지 업로드를 위한 form-data 형식으로 회원가입 controller
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class JoinController2 {

    private final MemberService memberService;

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
            @ModelAttribute @Valid JoinDTO joinDTO,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return "join";
        }
        JoinDTO joinedUser = memberService.join(joinDTO);
        return "redirect:/members/join-success";
    }

    @GetMapping("/join-success")
    public String joinSuccess() {
        return "joinSuccess"; // 단순 뷰 렌더링만!
    }
}
