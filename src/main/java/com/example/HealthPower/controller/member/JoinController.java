package com.example.HealthPower.controller.member;

import com.example.HealthPower.dto.login.JoinDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

//@Controller
@RestController
@RequiredArgsConstructor
@ResponseBody
@RequestMapping("/members")
public class JoinController {

    private final MemberService memberService;

    @PostMapping(value = "/join", consumes = MediaType.APPLICATION_JSON_VALUE)
    //@RequestBody를 통해 joinDTO에 바인딩
    //프로필 이미지 업로드를 위해 @RequestBody에서 @ModelAttribute로 변경
    public ResponseEntity join(@ModelAttribute @Valid JoinDTO joinDTO,
                               BindingResult bindingResult) throws IOException {

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

    /**
     * (선택) 가입 이후 프로필 이미지만 별도 수정하고 싶을 때
     */
    @PostMapping(
            value = "/{memberId}/profile-image",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("#memberId == principal.id or hasRole('ADMIN')")
    public ResponseEntity<?> uploadProfileImage(
            @PathVariable User user,
            @RequestPart("file") MultipartFile file
    ) {
        memberService.storeProfileImage(user, file);
        return ResponseEntity.ok().build();
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
