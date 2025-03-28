package com.example.HealthPower.service;

import com.example.HealthPower.dto.JoinDTO;
import com.example.HealthPower.entity.Authority;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.exception.DuplicateMemberException;
import com.example.HealthPower.jwt.JwtToken;
import com.example.HealthPower.jwt.JwtTokenProvider;
import com.example.HealthPower.repository.UserRepository;
import com.example.HealthPower.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    //bCryptPasswordEncoder = null로 인해 @Autowired 추가
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public JoinDTO join(JoinDTO joinDTO) {
        if (userRepository.findOneWithAuthoritiesByUserId(joinDTO.getUserId()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 아이디입니다.");
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .userId(joinDTO.getUserId())
                .password(bCryptPasswordEncoder.encode(joinDTO.getPassword()))
                .email(joinDTO.getEmail())
                .nickname(joinDTO.getNickname())
                .activated(true)
                .authorities(Collections.singleton(authority))
                .build();

        return JoinDTO.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(String userId) {
        return userRepository.findOneWithAuthoritiesByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUserId);
    }


    /*public void joinpre(JoinDTO joinDTO) {

        try {

            if (!joinDTO.getPassword().equals(joinDTO.getPasswordCheck())) {
                System.out.println("비밀번호 불일치");
                return;
            }
            //String userId = joinDTO.getUserId();

            String email = joinDTO.getEmail();

            String userId = joinDTO.getUsername();

            boolean isUserId = userRepository.existsByUserId(userId);
            if (isUserId) {
                System.out.println("exist userId");
                return;
            }

            String nickname = joinDTO.getNickname();

            String password = bCryptPasswordEncoder.encode(joinDTO.getPassword());

            String phoneNumber = joinDTO.getPhoneNumber();

            String address = joinDTO.getAddress();

            String birth = joinDTO.getBirth();

            String photo = joinDTO.getPhoto();

            Collection<GrantedAuthority> authorities = joinDTO.getAuthorities();

            LocalDateTime createdAt = LocalDateTime.now();

            User user = joinDTO.toEntity();

            System.out.println(user.getUserId() + ", " + user.getNickname() + ", " + user.getEmail());

            userRepository.save(user);


        } catch (Exception e) {
            System.out.println("이미 존재하는 사용자입니다." + joinDTO.getUserId());
            throw new RuntimeException("이미 존재하는 사용자 " + joinDTO.getUserId());
        }

    }*/

    private void validateDuplicateUser(User user) {
        Optional<User> findUser = userRepository.findByUserId(user.getUserId());
        if (findUser != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Transactional
    public JwtToken login(String username, String password) {
        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        return jwtToken;
    }
}
