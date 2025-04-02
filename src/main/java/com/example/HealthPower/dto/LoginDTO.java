package com.example.HealthPower.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
//controller에서 요청을 보내기 위해 사용할 LoginDto를 작성한다.
public class LoginDTO {

    @NotNull
    private String userId;

    @NotNull
    private String password;

    private boolean activated;
}
