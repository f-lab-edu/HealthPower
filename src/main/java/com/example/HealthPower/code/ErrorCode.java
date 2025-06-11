package com.example.HealthPower.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //인증/인가
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    //사용자 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    INVALID_USER_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 상태입니다."),

    //요청 유효성 검증
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "요청 데이터가 유효하지 않습니다."),
    BIND_ERROR(HttpStatus.BAD_REQUEST, "파라미터 바인딩에 실패했습니다."),

    //DB or 서버 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

}
