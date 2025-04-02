package com.example.HealthPower.controller;

import com.example.HealthPower.dto.LogoutDTO;
import com.example.HealthPower.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class DeleteController {

    private final MemberService memberService;

    //deleteMapping을 쓸 지, putMapping을 쓸 지?
    @PutMapping("/delete")
    public ResponseEntity deleteMember(HttpServletRequest request) throws Exception{
        try {
            memberService.deleteMember(request);
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
