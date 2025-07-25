package com.example.dto.server;

import com.example.entity.connect.ServerCheckingLog;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ServerCheckLogResponse {

    private String status;
    private String message;
    private LocalDateTime checkedAt;

    public static ServerCheckLogResponse from(ServerCheckingLog log) {
        return new ServerCheckLogResponse(
                log.getStatus(),
                log.getMessage(),
                log.getCheckedAt());
    }
}
