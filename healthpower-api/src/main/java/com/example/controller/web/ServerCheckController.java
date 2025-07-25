package com.example.controller.web;

import com.example.dto.server.ServerCheckLogResponse;
import com.example.service.ServerCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/server-check")
public class ServerCheckController {

    private final ServerCheckService serverCheckService;

    @GetMapping("/logs")
    public ResponseEntity<List<ServerCheckLogResponse>> getLogs() {
        return ResponseEntity.ok(serverCheckService.getLatestLogs());
    }
}
