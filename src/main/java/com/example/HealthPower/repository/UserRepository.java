package com.example.HealthPower.repository;

import com.example.HealthPower.dto.user.UserModifyDTO;
import com.example.HealthPower.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> save(UserModifyDTO userModifyDTO);

    Optional<User> findOneWithAuthoritiesByUserId(String userId);

    boolean existsByEmail(String email);

    Optional<User> findByUserId(String userId);

    boolean existsByNickname(String nickname);

    Optional<User> findById(Long id);

    boolean existsByUserId(String username);

    User getReferenceByUserId(String senderId);
}
