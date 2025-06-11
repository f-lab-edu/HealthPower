package com.example.HealthPower.controller.member;

import com.example.HealthPower.controller.api.SuccessResponse;
import com.example.HealthPower.dto.user.UserDTO;
import com.example.HealthPower.dto.user.UserModifyTestDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.exception.user.UserNotFoundException;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.repository.UserRepository;
import com.example.HealthPower.service.MemberService;
import com.example.HealthPower.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
/*@Controller*/
public class MyPageController {

    private final MemberService memberService;
    private final UserRepository userRepository;

    /* 마이페이지 보기 */
    /*@GetMapping("/myInfo")
    public Optional<User> searchMyInfo(UserDTO userDTO) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        String userId = userDetails.getUserId();

        Optional<User> infoResult = memberService.myInfo(userId);

        return infoResult;
    }*/

    /* 리팩토링 후 */
    @GetMapping("/myInfo")
    public ResponseEntity<SuccessResponse<UserDTO>> getMyInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findByUserId(userDetails.getUserId())
                .orElseThrow(() -> new UserNotFoundException(userDetails.getUserId()));

        UserDTO userDTO = UserDTO.of(user);
        return ResponseEntity.ok(SuccessResponse.of(userDTO, "회원 정보 조회 성공", HttpStatus.OK.value()));
    }

    //마이페이지 사진 제대로 들어오는지 뷰로 확인
    @GetMapping("/mypage")
    public ResponseEntity<Object> myPage(Authentication authentication) {
            String userId = SecurityUtil.getCurrentUsername()
                    .orElseThrow(() -> new RuntimeException("로그인 정보 없음"));

        UserDTO user = memberService.myinfo(userId);

            if (user == null) {
                throw new UserNotFoundException(userId);
            }
            return ResponseEntity.ok(user);
    }

    /* 마이페이지 수정 */
    @PutMapping(value = "/myInfoUpdate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> saveMyInfo(@ModelAttribute UserModifyTestDTO userModifyDTO,
                                             @RequestPart(required = false) MultipartFile photo) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        String userId = userDetails.getUserId();

        memberService.myInfoUpdate(userId, userModifyDTO);
        return ResponseEntity.ok("회원 정보가 성공적으로 업데이트되었습니다.");
    }

    /* 마이페이지 수정 */
    /*@PutMapping("/myInfoUpdate")
    public ResponseEntity<String> saveMyInfo(@RequestParam String username,
                                             @RequestParam String nickname,
                                             @RequestParam String password,
                                             @RequestParam String gender,
                                             @RequestParam String birth,
                                             @RequestParam String role,
                                             @RequestParam String email,
                                             @RequestParam(required = false) String address,
                                             @RequestParam(required = false) String phoneNumber,
                                             @RequestParam(required = false) Boolean activated,
                                             @RequestParam(required = false) Double balance,
                                             @RequestParam(required = false) MultipartFile photo,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

        try {
            String userId = userDetails.getUserId();
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자가 존재하지 않습니다."));

            UserModifyDTO userModifyDTO = UserModifyDTO.builder()
                    .userId(userId)
                    .username(username)
                    .nickname(nickname)
                    .password(password)
                    .gender(Gender.valueOf(gender))
                    .birth(LocalDate.parse(birth))
                    .role(Role.valueOf(role))
                    .email(email)
                    .address(address)
                    .phoneNumber(phoneNumber)
                    .activated(activated != null && activated)
                    .balance(balance)
                    .photo(photo)
                    .build();

            memberService.myInfoUpdate(userId, userModifyDTO);
            return ResponseEntity.ok("회원 정보가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            log.error("마이페이지 수정 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원 정보 수정 중 오류가 발생했습니다.");
        }
    }*/
}

