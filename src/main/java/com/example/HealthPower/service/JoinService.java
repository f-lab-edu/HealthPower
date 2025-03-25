package com.example.HealthPower.service;

import com.example.HealthPower.dto.JoinDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;

    //bCryptPasswordEncoder = null로 인해 @Autowired 추가
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void join(JoinDTO joinDTO) {

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

    }

    private void validateDuplicateUser(User user) {
        Optional<User> findUser = userRepository.findByUserId(user.getUserId());
        if (findUser != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }
}
