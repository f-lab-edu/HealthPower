package com.example.dto.token;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RefreshTokenDTO {
    @NotEmpty
    String refreshToken;
}
