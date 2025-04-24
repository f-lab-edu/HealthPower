package com.example.HealthPower.dto;

import com.example.HealthPower.entity.User;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.userType.Gender;
import com.example.HealthPower.userType.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Getter
@Setter //Setter를 가급적이면 쓰지 말라고 했는데, 로그인 시 임시로 userDTO를 세팅하기 위해서 넣어줌.
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
/* 회원정보 */
public class UserDTO {

    private Long id;

    private String photo;

    private String userId;

    private String email;

    private String password;

    private Gender gender;

    private LocalDate birth;

    private boolean activated;

    private List<AuthorityDTO> securityAuthList;

    private Collection<? extends GrantedAuthority> authorities;

    private Role role;

    public UserDTO(Long id, String userId, Role role) {
        this.id = id;
        this.userId = userId;
        this.role = role;
    }

    public UserDTO(UserDetailsImpl userDetails) {
        this.id = Long.valueOf(userDetails.getId());
        this.userId = userDetails.getUserId();
        this.email = userDetails.getEmail();
    }

    public UserDTO(Optional<User> findUser) {
        this.id = findUser.get().getId();
        this.userId = findUser.get().getUserId();
        this.password = findUser.get().getPassword();
        this.email = findUser.get().getEmail();
        this.authorities = findUser.get().getAuthorities();
        this.role = findUser.get().getRole();
    }
}
