package com.example.repository.iamport;

import com.example.entity.iamport.ImpOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImpOrderRepository extends JpaRepository<ImpOrder, Long> {
    Optional<ImpOrder> findByMerchantUid(String merchantUid);
}
