package com.example.HealthPower.dto;

import com.example.HealthPower.entity.User;
import com.example.HealthPower.util.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.processing.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//회원가입 시 사용할 DTO = signUpDTO
public class JoinDTO {

    private String userId;

    private String email;

    private String username;

    private String nickname;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @JsonProperty("passwordCheck")
    private String passwordCheck;

    private String phoneNumber;

    private String address;

    private String birth;

    private String photo;

    private Collection<GrantedAuthority> authorities;

    private LocalDateTime createdAt;

    private Set<AuthorityDTO> authorityDtoSet;

    public static JoinDTO from(User user) {
        if (user == null) return null;

        return JoinDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .password(user.getPassword())
/*                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .birth(user.getBirth())
                .photo(user.getPhoto())*/
                /*.createdAt(user.getCreatedAt())*/
                /*.authorityDtoSet(user.getAuthorities().stream()
                        .map(authority -> AuthorityDTO.builder()
                                .authorityName(authority.getAuthorityName()).build())
                        .collect(Collectors.toSet()))*/
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

