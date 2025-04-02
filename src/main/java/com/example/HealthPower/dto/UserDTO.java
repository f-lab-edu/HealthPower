package com.example.HealthPower.dto;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
@Setter //Setter를 가급적이면 쓰지 말라고 했는데, 로그인 시 임시로 userDTO를 세팅하기 위해서 넣어줌.
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private String userId;

    private String email;

    private String password;

    private Collection<GrantedAuthority> authorities;

    private boolean activated;

    private List<AuthorityDTO> securityAuthList;

}
