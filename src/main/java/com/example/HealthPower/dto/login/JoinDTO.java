package com.example.HealthPower.dto.login;

import com.example.HealthPower.customAnnotation.PasswordMatch;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.userType.Gender;
import com.example.HealthPower.userType.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
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
@PasswordMatch
//회원가입 시 사용할 DTO = signUpDTO
public class JoinDTO {
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영어와 숫자만 입력 가능합니다.")
    private String userId;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z]{2,10}$", message = "이름에는 숫자를 포함할 수 없습니다.")
    private String username;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @JsonProperty("passwordCheck")
    private String passwordCheck;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "휴대폰 번호는 숫자만 입력해야 합니다.")
    private String phoneNumber;

    private String address;

    @NotNull(message = "생년월일은 필수 입력 항목입니다.")
    private LocalDate birth;

    @NotNull(message = "성별은 필수 입력 값입니다.")
    private Gender gender;

    private MultipartFile photo;

    private Collection<GrantedAuthority> authorities;

    private LocalDateTime createdAt;

    private boolean activated;

    private Long balanace;

    @NotNull(message = "필수 입력 값입니다.")
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

