package com.example.HealthPower;

import com.example.HealthPower.entity.User;
import com.example.HealthPower.repository.UserRepository;
import com.example.HealthPower.userType.Gender;
import com.example.HealthPower.userType.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@ActiveProfiles("test")
@DataJpaTest
public class RepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void 회원저장_조회_테스트() {
        User user = User.builder()
                .userId("testId")
                .email("pankyo@gmail.com")
                .username("테스트판교")
                .nickname("판교닉")
                .password("12341234")
                .phoneNumber("01077881234")
                .address("")
                .photoUrl("")
                .photoPath("")
                .gender(Gender.MAN)
                .birth(LocalDate.of(1992, 12, 4))
                .balance(100000L)
                .createdAt(LocalDateTime.now())
                .role(Role.ADMIN)
                .activated(true).
                build();

        userRepository.save(user);

        Optional<User> foundId = userRepository.findByUserId(user.getUserId());
        Assertions.assertTrue(foundId.isPresent());
    }
}
