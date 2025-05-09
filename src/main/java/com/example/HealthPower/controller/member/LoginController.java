package com.example.HealthPower.controller.member;

import com.example.HealthPower.dto.login.LoginDTO;
import com.example.HealthPower.dto.user.UserDTO;
import com.example.HealthPower.jwt.JwtToken;
import com.example.HealthPower.repository.UserRepository;
import com.example.HealthPower.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController //@Controller에 @ResponseBody가 추가된 것 + Json 형태로 객체 데이터를 반환
@RequiredArgsConstructor
@RequestMapping("/members")
//@Controller는 주로 View를 반환하기 위해서 사용
public class LoginController {

    //Postman으로 이전에 DB에 저장했던 회원 정보(username, password)를 body에 담아서 "members/sign-in"으로 요청
    // 성공적으로 Access Token 발급
    // 발급받은 Access Token을 header에 넣어 "members/test"로 요청

    private final MemberService memberService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/login")
    public JwtToken Login(@RequestBody @Valid LoginDTO loginDTO) {

        String userId = loginDTO.getUserId();
        String password = loginDTO.getPassword();

        JwtToken jwtToken = memberService.login(userId, password);
        log.info("request username = {}, password = {}", userId, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}",
                jwtToken.getAccessToken(),
                jwtToken.getRefreshToken());
        return jwtToken;
    }

}
