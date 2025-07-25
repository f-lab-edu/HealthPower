package com.example.enumpackage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Role {
    USER("ROLE_USER"),
    TRAINER("ROLE_USER"),
    GYM_ADMIN("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private String Role;
}
