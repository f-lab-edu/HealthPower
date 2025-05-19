package com.example.HealthPower.dto.user;

import com.example.HealthPower.userType.Gender;
import com.example.HealthPower.userType.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
/* 회원정보 수정 */
public class UserModifyDTO {

    private String userId;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z]{2,10}$", message = "이름에는 숫자를 포함할 수 없습니다.")
    private String username;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    private MultipartFile photo;

    private String address;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "생년월일은 필수 입력 항목입니다.")
    private LocalDate birth;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "휴대폰 번호는 숫자만 입력해야 합니다.")
    private String phoneNumber;

    @NotNull(message = "성별은 필수 입력 값입니다.")
    private Gender gender;

    @NotNull(message = "필수 입력 값입니다.")
    private Role role;

    private Double balance;

    //SimpleGrantedAuthority는 기본적으로 생성자나 속성 기반의 역직렬화 방법을 제공하지 않아서 일단 String 형태로 지정
    //이 문자열을 나중에 SimpleGrantedAuthority로 변환
    private List<String> authorities;

    private boolean activated;

    public List<SimpleGrantedAuthority> getGrantedAuthorities() {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new) // String을 SimpleGrantedAuthority로 변환
                .collect(Collectors.toList());
    }

}
