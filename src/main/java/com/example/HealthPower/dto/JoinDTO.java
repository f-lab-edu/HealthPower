package com.example.HealthPower.dto;

import com.example.HealthPower.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.catalina.User;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class JoinDTO {

    private String userId;

    private String email;

    private String name;

    private String nickname;

    private String password;

    private String phoneNumber;

    private String address;

    private String birth;

    private String photo;

    private String role;

    private LocalDateTime createdAt;

    public UserEntity toEntity(String password) {
        return UserEntity.builder()
                .userId(userId)
                .email(email)
                .name(name)
                .nickName(nickname)
                .password(password)
                .phoneNumber(phoneNumber)
                .address(address)
                .birth(birth)
                .photo(photo)
                .role(role)
                .createdAt(createdAt)
                .build();
    }
}
