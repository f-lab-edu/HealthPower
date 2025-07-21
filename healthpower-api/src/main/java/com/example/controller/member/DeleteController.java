package com.example.controller.member;

import com.example.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/members")
//회원탈퇴는 블랙리스트 방식으로 구현(Redis)
//탈퇴 요청 처리: 사용자가 탈퇴 요청을 하면 해당 사용자의 JWT를 블랙리스트에 추가하고,
// 해당 JWT가 포함된 요청이 들어오면 이를 차단합니다. + 사용자가 탈퇴를 요청하면 해당 사용자의 JWT에 포함된 jti 값을 블랙리스트에 추가하고, 사용자의 관련 데이터를 삭제합니다.
public class DeleteController {

    private final MemberService memberService;

    @PostMapping("/delete")
    public ResponseEntity deleteMember(HttpServletRequest request) throws Exception {

        memberService.deleteMember(request);

        return ResponseEntity.ok("회원탈퇴 완료");
    }
}
