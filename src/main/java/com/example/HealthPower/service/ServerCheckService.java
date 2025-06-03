package com.example.HealthPower.service;

import com.example.HealthPower.dto.server.ServerCheckLogResponse;
import com.example.HealthPower.entity.connect.ServerCheckingLog;
import com.example.HealthPower.repository.ServerCheckLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServerCheckService {

    private final ServerCheckLogRepository serverCheckLogRepository;

    public void saveLog(String status, String message) {
        ServerCheckingLog log = ServerCheckingLog.builder()
                .status(status)
                .message(message)
                .checkedAt(LocalDateTime.now())
                .build();

        serverCheckLogRepository.save(log);
    }

    public List<ServerCheckLogResponse> getLatestLogs() {
        return serverCheckLogRepository.findTop100ByOrderByCheckedAtDesc()
                .stream()
                .map(ServerCheckLogResponse::from)
                .collect(Collectors.toList());
    }
}
