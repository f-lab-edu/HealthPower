package com.example.HealthPower.service;

import com.example.HealthPower.dto.JoinDTO;
import com.example.HealthPower.entity.UserEntity;
import com.example.HealthPower.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JoinService {

    private PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public JoinService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void join(JoinDTO joinDTO) {

        String email = joinDTO.getEmail();

        String name = joinDTO.getName();

        String nickname = joinDTO.getNickname();

        String password = joinDTO.getPassword();

        String encodedPasswrod = passwordEncoder.encode(joinDTO.getPassword());

        String phoneNumber = joinDTO.getPhoneNumber();

        String address = joinDTO.getAddress();

        String birth = joinDTO.getBirth();

        String photo = joinDTO.getPhoto();

        String role = joinDTO.getRole();

        LocalDateTime createdAt = joinDTO.getCreatedAt();

        UserEntity userEntity = joinDTO.toEntity(encodedPasswrod);

        userRepository.save(userEntity);

        System.out.println("save success in service");

    }
}
