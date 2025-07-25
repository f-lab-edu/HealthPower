package com.example.exception.user;

import com.example.enumpackage.ErrorCode;

public class UserNotFoundException extends UserException {
    public UserNotFoundException(String userId) {
        super(ErrorCode.USER_NOT_FOUND, " 해당 사용자를 찾을 수 없습니다 : " + userId);
    }
}
