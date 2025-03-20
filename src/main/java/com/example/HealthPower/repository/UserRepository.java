package com.example.HealthPower.repository;

import com.example.HealthPower.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, BigInteger> {

    Optional<UserEntity> findByEmail(String email);


}
