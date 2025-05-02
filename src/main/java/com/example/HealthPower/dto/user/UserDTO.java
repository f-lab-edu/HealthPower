package com.example.HealthPower.dto.user;

import com.example.HealthPower.dto.AuthorityDTO;
import com.example.HealthPower.userType.Gender;
import com.example.HealthPower.userType.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Setter //Setter를 가급적이면 쓰지 말라고 했는데, 로그인 시 임시로 userDTO를 세팅하기 위해서 넣어줌.
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
/* 회원정보 */
public class UserDTO {

    private Long id;

    /*private String photo;*/

    private MultipartFile photo;

    private String userId;

    private String email;

    private String password;

    private Gender gender;

    private LocalDate birth;

    private Collection<GrantedAuthority> authorities;

    private boolean activated;

    private List<AuthorityDTO> securityAuthList;

    public UserDTO(Long id, String userId, Role role) {
    }
}
