package com.example.dto.user;

import com.example.enumpackage.Gender;
import com.example.enumpackage.Role;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Setter
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

    private String imageUrl;

    private String address;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @Pattern(regexp = "^$|.{8,}", message = "새 비밀번호는 8자 이상이어야 합니다.")
    private String newPassword;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "생년월일은 필수 입력 항목입니다.")
    private LocalDate birth;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "휴대폰 번호는 숫자만 입력해야 합니다.")
    private String phoneNumber;

    @NotNull(message = "성별은 필수 입력 값입니다.")
    private Gender gender;

    @NotNull(message = "필수 입력 값입니다.")
    private Role role;

    private Long balance;

    //SimpleGrantedAuthority는 기본적으로 생성자나 속성 기반의 역직렬화 방법을 제공하지 않아서 일단 String 형태로 지정
    //이 문자열을 나중에 SimpleGrantedAuthority로 변환
    private List<String> authorities;

    private boolean activated;

    public record ChargeRequest(
            @Positive(message = "충전 금액은 1원 이상이어야 합니다.")
            Long amount) {
    }

}
