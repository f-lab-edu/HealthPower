package com.example.HealthPower.service;

import com.example.HealthPower.dto.JoinDTO;
import com.example.HealthPower.entity.UserEntity;
import com.example.HealthPower.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;

    //bCryptPasswordEncoder = null로 인해 @Autowired 추가
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void join(JoinDTO joinDTO) {

        String email = joinDTO.getEmail();

        String name = joinDTO.getName();

        String nickname = joinDTO.getNickname();

        //String password = joinDTO.getPassword();

        String password = bCryptPasswordEncoder.encode(joinDTO.getPassword());

        String phoneNumber = joinDTO.getPhoneNumber();

        String address = joinDTO.getAddress();

        String birth = joinDTO.getBirth();

        String photo = joinDTO.getPhoto();

        Collection<GrantedAuthority> authorities = joinDTO.getAuthorities();

        LocalDateTime createdAt = LocalDateTime.now();

        System.out.println(createdAt);

        UserEntity userEntity = joinDTO.toEntity();

        userRepository.save(userEntity);

        System.out.println("save success in service");

    }
}
