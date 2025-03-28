package com.example.HealthPower.impl;

import com.example.HealthPower.entity.User;
import com.example.HealthPower.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUserId(username).orElseThrow(() -> new
                UsernameNotFoundException("일치하는 아이디가 없습니다."));

        return (UserDetails) user;
    }
}
