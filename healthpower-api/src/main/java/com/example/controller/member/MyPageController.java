package com.example.controller.member;

import com.example.controller.api.SuccessResponse;
import com.example.dto.user.UserDTO;
import com.example.dto.user.UserModifyDTO;
import com.example.entity.User;
import com.example.exception.user.UserNotFoundException;
import com.example.impl.UserDetailsImpl;
import com.example.repository.UserRepository;
import com.example.service.MemberService;
import com.example.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Slf4j
/*@RestController*/
@RequestMapping("/members")
@RequiredArgsConstructor
@Controller
public class MyPageController {

    private final MemberService memberService;
    private final UserRepository userRepository;

    /* 리팩토링 후 */
    @GetMapping("/myInfo")
    public ResponseEntity<SuccessResponse<UserDTO>> getMyInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findByUserId(userDetails.getUserId())
                .orElseThrow(() -> new UserNotFoundException(userDetails.getUserId()));

        UserDTO userDTO = UserDTO.of(user);
        return ResponseEntity.ok(SuccessResponse.of(userDTO, "회원 정보 조회 성공", HttpStatus.OK.value()));
    }

    //마이페이지 사진경로 json 응답
/*    @GetMapping("/mypage")
    public ResponseEntity<Object> myPage(Authentication authentication) {
            String userId = SecurityUtil.getCurrentUsername()
                    .orElseThrow(() -> new RuntimeException("로그인 정보 없음"));

        UserDTO user = memberService.myinfo(userId);

            if (user == null) {
                throw new UserNotFoundException(userId);
            }
            return ResponseEntity.ok(user);
    }*/

    //마이페이지 사진 제대로 들어오는지 뷰로 확인
    @GetMapping("/mypage")
    public String myPage(Authentication authentication, Model model) {
        String userId = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("로그인 정보 없음"));

        UserDTO user = memberService.myinfo(userId);

        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        model.addAttribute("userModifyDTO", user);
        return "myPage";
    }

    /* 마이페이지 수정 */
    /*@PutMapping(value = "/myInfoUpdate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> saveMyInfo(@ModelAttribute UserModifyDTO userModifyDTO,
                                             @RequestPart(required = false) MultipartFile photo) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        String userId = userDetails.getUserId();

        memberService.myInfoUpdate(userId, userModifyDTO);
        return ResponseEntity.ok("회원 정보가 성공적으로 업데이트되었습니다.");
    }*/

    /* 마이페이지 수정 */
    @PutMapping(value = "/myInfoUpdate2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public String saveMyInfo2(@Valid @ModelAttribute UserModifyDTO userModifyDTO,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserDetailsImpl userDetails,
                              RedirectAttributes redirectAttributes) throws IOException {

        if (bindingResult.hasErrors()) {
            return "myPage";
        }

        String userId = userDetails.getUserId();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자가 존재하지 않습니다."));

        memberService.myInfoUpdate(userId, userModifyDTO);

        redirectAttributes.addFlashAttribute("msg", "회원정보 수정이 완료되었습니다.");

        return "redirect:/members/menu";
    }
}

