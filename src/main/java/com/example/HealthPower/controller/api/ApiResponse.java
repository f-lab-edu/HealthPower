package com.example.HealthPower.controller.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final int status;
    private final String message;
    private final T data;
}
