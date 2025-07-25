package com.example.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseFactory {
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "요청 성공", data));
    }

    public static <T> ResponseEntity<ApiResponse<Void>> success() {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "요청 성공", null));
    }

    public static <T> ResponseEntity<ApiResponse<Void>> error(HttpStatus status, String message) {
        return ResponseEntity.ok(new ApiResponse<>(status.value(), message, null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message, T data) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), message, data));
    }

}
