package com.example.HealthPower.dto;

import com.example.HealthPower.userType.Gender;
import com.example.HealthPower.userType.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
/* 회원정보 수정 */
public class UserModifyDTO {
    private Long id;

    private String userId;

    private String username;

    private String nickname;

    private String photo;

    private String email;

    private String password;

    private LocalDate birth;

    private Gender gender;

    private Role role;

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
