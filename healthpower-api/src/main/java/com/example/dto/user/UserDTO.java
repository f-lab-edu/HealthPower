package com.example.dto.user;

import com.example.dto.AuthorityDTO;
import com.example.entity.User;
import com.example.enumpackage.Gender;
import com.example.enumpackage.Role;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter //Setter를 가급적이면 쓰지 말라고 했는데, 로그인 시 임시로 userDTO를 세팅하기 위해서 넣어줌.
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
/* 회원정보 */
public class UserDTO {

    private Long id;

    private String imageUrl;

    private String userId;

    private String username;

    private String nickname;

    private String email;

    private String password;

    private String newPassword;

    private String address;

    private String phoneNumber;

    private Gender gender;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birth;

    private boolean activated;

    private Long balance;

    private Role role;

    private List<AuthorityDTO> securityAuthList;

    public UserDTO(Long id, String userId, Role role) {
    }

    public static UserDTO of(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .imageUrl(user.getImageUrl())
                .nickname(user.getNickname())
                .address(user.getAddress())
                .balance(user.getBalance())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .gender(user.getGender())
                .birth(user.getBirth())
                .activated(user.isActivated())
                .role(user.getRole())
                .build();
    }
}
