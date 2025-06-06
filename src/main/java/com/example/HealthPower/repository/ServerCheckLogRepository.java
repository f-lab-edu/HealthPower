package com.example.HealthPower.repository;

import com.example.HealthPower.entity.connect.ServerCheckingLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServerCheckLogRepository extends JpaRepository<ServerCheckingLog, Long> {
    List<ServerCheckingLog> findTop100ByOrderByCheckedAtDesc();
}
