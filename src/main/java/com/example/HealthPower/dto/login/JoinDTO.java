package com.example.HealthPower.dto.login;

import com.example.HealthPower.entity.User;
import com.example.HealthPower.userType.Gender;
import com.example.HealthPower.userType.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//회원가입 시 사용할 DTO = signUpDTO
public class JoinDTO {
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String userId;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String username;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @JsonProperty("passwordCheck")
    private String passwordCheck;

    private String phoneNumber;

    private String address;

    private LocalDate birth;

    private Gender gender;

    private MultipartFile photo;

    private Collection<? extends GrantedAuthority> authorities;

    private LocalDateTime createdAt;

    private boolean activated;

    private Role role;

    public static JoinDTO from(User user) {
        if (user == null) return null;

        // LocalDateTime 객체 생성
        LocalDateTime localDateTime = LocalDateTime.now();

        // 날짜와 시간 포맷 정의 (yyyy-MM-dd HH:mm:ss 형식)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // LocalDateTime을 String으로 변환
        String formattedDate = localDateTime.format(formatter);

        return JoinDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .password(user.getPassword())
                .activated(user.isActivated())
                .role(user.getRole())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .birth(user.getBirth())
                .createdAt(LocalDateTime.now())
                .build();
    }

}

    /*public User toEntity() {
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
                .build();*/

