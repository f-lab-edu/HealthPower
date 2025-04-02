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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//UserDetailsService => Spring Security에서 유저의 정보를 불러오기 위해서 구현해야하는 인터페이스
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    //로그인 시 DB에서 유저정보와 권한정로를 가져옴
    //유저의 정보를 불러와서 UserDetails로 리턴
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return userRepository.findByUserId(userId)
                .map(this::createUserDetails)
                .map(user -> createUser(userId, user))
                .orElseThrow(()->new UsernameNotFoundException(userId + " : 해당 아이디를 가진 회원을 찾을 수 없습니다."));
    }

    // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 return
    private User createUserDetails(User user) {
        return User.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .password(user.getPassword())
                .activated(user.isActivated())
                .build();
    }

    private org.springframework.security.core.userdetails.User createUser(String userId, User user) {
        if (!user.isActivated()) {
            throw new RuntimeException(userId + " -> 활성화되어 있지 않습니다.");
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                grantedAuthorities);
    }
}
