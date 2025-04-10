package com.example.HealthPower.service;

import com.example.HealthPower.dto.JoinDTO;
import com.example.HealthPower.dto.UserDTO;
/*import com.example.HealthPower.entity.Authority;*/
import com.example.HealthPower.dto.UserModifyDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.exception.DuplicateMemberException;
import com.example.HealthPower.jwt.JwtToken;
import com.example.HealthPower.jwt.JwtTokenProvider;
import com.example.HealthPower.repository.UserRepository;
import com.example.HealthPower.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    //bCryptPasswordEncoder = null로 인해 @Autowired 추가
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /* 회원가입 */
    @Transactional
    public JoinDTO join(JoinDTO joinDTO) {
        if (userRepository.findByUserId(joinDTO.getUserId()).orElse(null) != null) {
            log.info("이미 가입되어 있는 아이디");
            throw new DuplicateMemberException("이미 가입되어 있는 아이디입니다.");
        }

        User user = User.builder()
                .username(joinDTO.getUsername())
                .userId(joinDTO.getUserId())
                .password(bCryptPasswordEncoder.encode(joinDTO.getPassword()))
                .email(joinDTO.getEmail())
                .nickname(joinDTO.getNickname())
                .activated(true)
                .role(joinDTO.getRole())
                .birth(joinDTO.getBirth())
                .gender(joinDTO.getGender())
                .createdAt(joinDTO.getCreatedAt())
                .authorities(joinDTO.getAuthorities()) //당연히 null값이 올 수 밖에 없음.
                .build();

        User save = userRepository.save(user);

        return JoinDTO.from(save);
    }

    // 리프레시 토큰 Redis에서 제거(구현x)
    // 액세스 토큰 Redis에 저장(로그아웃 확인용)
    // 로그아웃과 회원탈퇴에서 모두 사용 -> type이 delete면 회원을 찾아 db에서 영구 삭제
    /*public void logout(String accessToken) {
        try {
            jwtUtil.validateToken(accessToken);
        } catch (JwtExceptionHandler e) {
            throw new JwtExceptionHandler(ErrorStatus.NOT_VALID_TOKEN.getMessage());
        }

        String email = jwtUtil.getEmail(accessToken);

        if (redisTemplate.opsForValue().get("RT" + email) != null) {
            redisTemplate.delete("RT" + email);
        }

        Long expiration = JwtUtil.getExpiration(accessToken);
        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        if (type.equals("DELETE")) {
            Member member = userRepository.findByEmail(email)
                    .orElseThrow(() -> new MemberHandler(ErrorStatus.NO_MEMBER_EXIST));
            memberRepository.delete(member);
        }
    }*/

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(String userId) {
        return userRepository.findOneWithAuthoritiesByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUserId);
    }

    private void validateDuplicateUser(User user) {
        Optional<User> findUser = userRepository.findByUserId(user.getUserId());
        if (findUser != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Transactional
    public JwtToken login(UserDTO userDTO, String userId, String password) {

        Optional<User> findUserId = userRepository.findByUserId(userId);

        if (findUserId.isEmpty()) {
            System.out.println("존재하지 않는 사용자입니다.");
            findUserId.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        }

        //기존의 userDTO에서 비밀번호와 현재 입력한 비밀번호가 일치하는지 비교
        if (bCryptPasswordEncoder.matches(bCryptPasswordEncoder.encode(password), findUserId.get().getPassword())) {
            System.out.println("계정의 비밀번호가 올바르지 않습니다.");
            findUserId.orElseThrow(() -> new IllegalArgumentException("계정의 비밀번호가 올바르지 않습니다"));
        }

        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, password);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        try {
            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            JwtToken jwtToken = jwtTokenProvider.generateToken(authentication, userDTO);
            return jwtToken;
        } catch (Exception e) {
            log.error("에러발생{}", e.getMessage());
            e.printStackTrace();
        }

        return null;

    }

    /* 마이페이지 (회원 상세 조회) */
    public Optional<User> myInfo(String userId) {
        return userRepository.findByUserId(userId);
    }

    /* 마이페이지 정보 업데이트 */
    public User myInfoUpdate(UserModifyDTO userModifyDTO) {
        //DTO를 Entity형태로 저장해야함.(JPA는 엔티티 객체를 DB에 저장하기 때문에)

        //1.DTO에서 User 엔티티 객체로 변환
        User user = userRepository.findByUserId(userModifyDTO.getUserId())
                .orElseThrow(()->new RuntimeException("조회되는 회원 아이디가 없습니다."));

        // 2. 변환된 User 엔티티에 DTO 값 업데이트
        // 엔티티에선 @Setter를 사용안하는 걸 권장하는데, 그럼 정보 수정을 다른 방식으로 하는 방법이 있나?
        user.setUsername(userModifyDTO.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userModifyDTO.getPassword()));
        user.setGender(userModifyDTO.getGender());
        user.setEmail(userModifyDTO.getEmail());
        user.setNickname(userModifyDTO.getNickname());
        user.setBirth(userModifyDTO.getBirth());
        user.setRole(userModifyDTO.getRole());
        user.setActivated(userModifyDTO.isActivated());
        //user.setAuthorities(authorities); // 권한 업데이트 에러(타입 불일치)

        return userRepository.save(user);

    }

    //회원 탈퇴
    //회원 탈퇴는 RefreshToken, AccessToken, 사용자 정보를 모두 삭제해주면 됨.
    public void deleteMember(HttpServletRequest request) {

        String refreshToken = request.getHeader("Refresh-Token");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            System.out.println("리프레쉬 토큰이 만료되었음.");
            return;
        }

        // 사용자 정보 받아오기
        /*UserDTO userDto = jwtService.getUser(Authorization)
                .orElseThrow(() -> new NoSuchElementException("getUserInfo :: 존재하지 않는 사용자입니다."));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException());

        userRepository.delete(userDTO);
*/
    }
}
