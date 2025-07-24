package com.example.repository;

import com.example.dto.user.UserModifyDTO;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> save(UserModifyDTO userModifyDTO);

    Optional<User> findOneWithAuthoritiesByUserId(String userId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findById(Long id);

    Optional<User> findByUserId(String userId);

    boolean existsByUserId(String username);

    User getReferenceByUserId(String senderId);

    List<User> findAllByUserIdIn(List<String> userIds);
}
