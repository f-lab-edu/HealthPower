package com.example.HealthPower.service;

import com.example.HealthPower.entity.User;
import com.example.HealthPower.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    //로그인 시 DB에서 유저정보와 권한정로를 가져옴
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return userRepository.findOneWithAuthoritiesByUserId(userId)
                //.map(this::createUserDetails)
                .map(user -> createUser(userId, user))
                .orElseThrow(()->new UsernameNotFoundException(userId + " : 해당 아이디를 가진 회원을 찾을 수 없습니다."));
    }

    // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 return
    private User createUserDetails(User user) {
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

    private org.springframework.security.core.userdetails.User createUser(String username, User user) {
        if (!user.isActivated()) {
            throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");
        }
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                grantedAuthorities);
    }
}
