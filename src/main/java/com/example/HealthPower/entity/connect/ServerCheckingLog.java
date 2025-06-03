package com.example.HealthPower.entity.connect;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "health_check_log")
@Getter
@Setter
public class ServerCheckingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    private String message;

    private LocalDateTime checkedAt;

    protected ServerCheckingLog() {

    }

    @Builder
    public ServerCheckingLog(String status, String message, LocalDateTime checkedAt) {
        this.status = status;
        this.message = message;
        this.checkedAt = checkedAt;
    }
}

