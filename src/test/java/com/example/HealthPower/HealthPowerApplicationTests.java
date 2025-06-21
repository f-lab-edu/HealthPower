package com.example.HealthPower;

import com.example.HealthPower.dto.user.UserDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.jwt.JwtTokenProvider;
import com.example.HealthPower.repository.UserRepository;
import com.example.HealthPower.service.MemberService;
import com.example.HealthPower.userType.Gender;
import com.example.HealthPower.userType.Role;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HealthPowerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private UserDetailsImpl userDetails;

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {

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
                .balance(1000L)
                .createdAt(LocalDateTime.now())
                .role(Role.ADMIN)
                .activated(true).
                build();

        userRepository.save(user);
        userDetails = new UserDetailsImpl(user);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

    }

    @Disabled("임시로 contextLoads 테스트 제외 - AWS Bean 주입 문제")

    @WithMockUser(username = "testId", roles = {"ADMIN"})

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        userRepository.deleteAll();
    }

    @Test
    @Disabled("수정 중")
    void 마이페이지_조회_테스트() throws Exception {

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        // 필요한 UserDTO 생성 (예시)
        UserDTO userDTO = UserDTO.builder()
                .userId(userDetails.getUserId())
                .build();

        String token = String.valueOf(jwtTokenProvider.generateToken(authentication, userDTO));

        mockMvc.perform(get("/members/myinfo")
                .cookie(new Cookie("Authorization", token)))
                .andExpect(status().isOk());
    }

    @Test
    void contextLoads() {
    }

}
