package com.example.repository;

import com.example.entity.log.ExpiredCouponLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpiredCouponRepository extends JpaRepository<ExpiredCouponLog, Long> {

}
