package com.example.HealthPower.userType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Gender {
    MAN("남자"),
    WOMAN("여자");

    private String Gender;
}
