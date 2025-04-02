package com.example.HealthPower.userType;

import lombok.Getter;

@Getter
public enum Role {
    USER("USER"),
    TRAINER("TRAINER"),
    GYM_ADMIN("GYM_ADMIN"),
    ADMIN("ADMIN");

    private String Role;

    private Role(String Role) {
        this.Role = Role;
    }
}
