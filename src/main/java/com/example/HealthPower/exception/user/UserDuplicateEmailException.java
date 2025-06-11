package com.example.HealthPower.exception.user;

import com.example.HealthPower.code.ErrorCode;

public class UserDuplicateEmailException extends UserException {
    public UserDuplicateEmailException(String email) {
        super(ErrorCode.DUPLICATE_EMAIL, "이미 사용 중인 이메일입니다 : " + email);
    }
}
