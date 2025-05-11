package com.example.HealthPower.repository;

import com.example.HealthPower.entity.payment.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    List<TransactionHistory> findByUserId(String userId);
}
