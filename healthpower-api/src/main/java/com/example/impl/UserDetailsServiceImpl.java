package com.example.impl;

import com.example.entity.User;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
//UserDetailsService => Spring Security에서 유저의 정보를 불러오기 위해서 구현해야하는 인터페이스
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    //로그인 시 DB에서 유저정보와 권한정로를 가져옴
    //유저의 정보를 불러와서 UserDetails로 리턴
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException(userId + " : 회원을 찾을 수 없습니다."));

        if (!user.isActivated()) {
            throw new RuntimeException(userId + " -> 활성화되어 있지 않습니다.");
        }

        log.info(">>> DB nickname={}, email={}", user.getNickname(), user.getEmail());

        /* ★ 내 UserDetailsImpl 로 반환 */
        return UserDetailsImpl.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .password(user.getPassword())
                .nickname(user.getNickname())    // ← 값 채움
                .email(user.getEmail())          // ← 값 채움
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .build();
    }

    // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 return
    private User createUserDetails(User user) {

        //spring Security가 인식할 수 있도록 SimpleGrantedAuthority로 변환
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return User.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .activated(user.isActivated())
                .build();
    }

    private org.springframework.security.core.userdetails.User createUser(String userId, User user) {

        if (!user.isActivated()) {
            throw new RuntimeException(userId + " -> 활성화되어 있지 않습니다.");
        }

        List<GrantedAuthority> grantedAuthorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                grantedAuthorities);
    }
}
