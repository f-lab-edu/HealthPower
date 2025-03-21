package com.example.HealthPower.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class LoginDTO {
    
    private String userId;

    private String password;
}
