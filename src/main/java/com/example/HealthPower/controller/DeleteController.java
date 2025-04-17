package com.example.HealthPower.controller;

import com.example.HealthPower.dto.LogoutDTO;
import com.example.HealthPower.service.BlackListService;
import com.example.HealthPower.service.MemberService;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    @Autowired
    private final BlackListService blackListService;

    //deleteMapping을 쓸 지, putMapping을 쓸 지?
    @PutMapping("/delete")
    public ResponseEntity deleteMember(@RequestHeader("Authorization") String token, HttpServletRequest request) throws Exception{
        try {
            // JWT에서 jti 추출
            String jti = Jwts.parser()
                    .setSigningKey("secretKey")
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody()
                    .getId();

            // 해당 jti를 블랙리스트에 추가
            blackListService.addToBlacklist(jti);

            memberService.deleteMember(request);

            log.info("{}", "회원탈퇴 완료");

            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //회원이 삭제되면 토큰도 삭제되게끔 이벤트리스너를 사용
    /*@EventListener
    public void deleteToken() {

    }*/
}
