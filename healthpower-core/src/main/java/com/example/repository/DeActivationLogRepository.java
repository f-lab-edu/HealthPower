package com.example.repository;

import com.example.entity.log.DeActivationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeActivationLogRepository extends JpaRepository<DeActivationLog, Long> {

}
