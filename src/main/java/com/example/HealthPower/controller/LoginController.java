package com.example.HealthPower.controller;

import com.example.HealthPower.dto.LoginDTO;
import com.example.HealthPower.dto.UserDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.jwt.JwtToken;
import com.example.HealthPower.repository.UserRepository;
import com.example.HealthPower.service.MemberService;
import com.example.HealthPower.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public JwtToken signIn(@RequestBody @Valid LoginDTO loginDTO) {

        //id를 직접 입력하는 것이 아니라 db자체에 저장되어있는 id값으로 가져와야함(or userId로 조회하는 방식으로 만들던가)
        Long id = loginDTO.getId();
        String userId = loginDTO.getUserId();
        String password = loginDTO.getPassword();

        //login메서드를 사용하기 위해서 userDTO객체 생성(이걸 꼭 넣어줘야하나?)
        UserDTO userDTO = new UserDTO();

        userDTO.setId(id);
        userDTO.setUserId(userId);
        userDTO.setPassword(bCryptPasswordEncoder.encode(password));

        JwtToken jwtToken = memberService.login(userDTO, userId, password);
        log.info("request id={}, username = {}, password = {}", id, userId, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}",
                jwtToken.getAccessToken(),
                jwtToken.getRefreshToken());
        return jwtToken;
    }

}
