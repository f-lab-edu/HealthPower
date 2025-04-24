package com.example.HealthPower.userType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Role {
    USER("ROLE_USER"),
    TRAINER("ROLE_USER"),
    GYM_ADMIN("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private String Role;

    public GrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority(this.name());
    }
}
