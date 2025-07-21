package com.example.exception.user;

import com.example.enumpackage.ErrorCode;

public class UserDuplicateEmailException extends UserException {
    public UserDuplicateEmailException(String email) {
        super(ErrorCode.DUPLICATE_EMAIL, "이미 사용 중인 이메일입니다 : " + email);
    }
}
