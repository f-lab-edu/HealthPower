package com.example.HealthPower.dto;

import com.example.HealthPower.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.processing.Pattern;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@AllArgsConstructor
public class JoinDTO {

    private String userId;

    private String email;

    private String username;

    private String nickname;

    @NotBlank(message="비밀번호는 필수 입력 값입니다.")
    @JsonProperty("password")
    private String password;

    @NotBlank(message="비밀번호는 필수 입력 값입니다.")
    @JsonProperty("passwordCheck")
    private String passwordCheck;

    private String phoneNumber;

    private String address;

    private String birth;

    private String photo;

    private Collection<GrantedAuthority> authorities;

    private LocalDateTime createdAt;

    public User toEntity() {
        return User.builder()
                .userId(userId)
                .email(email)
                .username(username)
                .nickname(nickname)
                .password(password)
                .phoneNumber(phoneNumber)
                .address(address)
                .birth(birth)
                .photo(photo)
                .authorities(authorities)
                .createdAt(createdAt)
                .build();
    }
}
