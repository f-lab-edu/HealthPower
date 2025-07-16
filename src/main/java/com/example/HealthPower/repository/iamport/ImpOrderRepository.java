package com.example.HealthPower.repository.iamport;

import com.example.HealthPower.entity.iamport.ImpOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImpOrderRepository extends JpaRepository<ImpOrder, Long> {
    Optional<ImpOrder> findByMerchantUid(String merchantUid);
}
