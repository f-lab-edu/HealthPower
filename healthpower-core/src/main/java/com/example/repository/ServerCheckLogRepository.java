package com.example.repository;

import com.example.entity.connect.ServerCheckingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ServerCheckLogRepository extends JpaRepository<ServerCheckingLog, Long> {
    List<ServerCheckingLog> findTop100ByOrderByCheckedAtDesc();
}
