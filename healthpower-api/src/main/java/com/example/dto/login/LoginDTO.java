package com.example.dto.login;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    private Long id;

    @NotNull
    private String userId;

    @NotNull
    private String password;

    private boolean activated;
}
