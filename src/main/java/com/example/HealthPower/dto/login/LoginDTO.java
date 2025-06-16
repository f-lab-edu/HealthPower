package com.example.HealthPower.dto.login;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
//controller에서 요청을 보내기 위해 사용할 LoginDto를 작성한다.
public class LoginDTO {

    private Long id;

    @NotNull
    private String userId;

    @NotNull
    private String password;

    private boolean activated;
}
