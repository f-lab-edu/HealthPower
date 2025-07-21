package com.example.exception.user;

import com.example.enumpackage.ErrorCode;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

    private final ErrorCode errorCode;

    public UserException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public UserException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
