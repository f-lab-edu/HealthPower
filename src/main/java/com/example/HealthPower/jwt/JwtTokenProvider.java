package com.example.HealthPower.jwt;

import com.example.HealthPower.dto.UserDTO;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
//Spring Security와 JWT 토큰을 사용하여 인증과 권한 부여를 처리하는 클래스
//JWT 토큰의 생성, 복호화(디코딩), 검증 기능을 구현
public class JwtTokenProvider {

    private final Key key;

    private final UserRepository userRepository;

    // application.yml(properties)에서 secret 값 가져와서 key에 저장
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, UserRepository userRepository) {
        this.userRepository = userRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // User 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드

    // 주어진 Access token을 복호화하여 사용자의 인증 정보(Authentication)를 생성
    // 토큰의 Claims에서 권한 정보를 추출하고, User 객체를 생성하여 Authentication 객체로 반환
    // Collection<? extends GrantedAuthority>로 리턴받는 이유
    // 권한 정보를 다양한 타입의 객체로 처리할 수 있고, 더 큰 유연성과 확장성을 가질 수 있음
    public JwtToken generateToken(Authentication authentication, UserDTO userDTO) {

        if (authentication == null) {
            log.info("Authentication object is null");
        } else {
            log.info("Authentication: " + authentication.getName());
        }

        //권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성 + 유효 시간 설정
        Date accessTokenExpiry = new Date(now + 86400000); //1일
        Date refreshTokenExpiry = new Date(now + 864000000); //10일

        log.info("Key used to sign the token: " + Arrays.toString(key.getEncoded())); //key 출력

        // 🔹 공통 클레임 구성
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userDTO.getId());
        claims.put("userId", userDTO.getUserId());
        claims.put("email", userDTO.getEmail());
        claims.put("auth", authorities);

        //accessToken을 통해 jwt토큰을 복호화하기 때문에 여기서 내가 원하는 정보를 설정
        String accessToken = Jwts.builder()
                //.setSubject(authentication.getName())
                .setClaims(claims)
                .setSubject(userDTO.getUserId())
                .setIssuedAt(new Date(now))
                //테스트용
                //.claim("auth", "test_admin")
                //.claim("id", userDTO.getId())
                .signWith(key, SignatureAlgorithm.HS256) //key 값이 서버에서 검증하는 key 값과 동일해야 함.
                .setExpiration(accessTokenExpiry)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                //.setSubject(authentication.getName())
                .setSubject(userDTO.getUserId())
                .claim("id", userDTO.getId())
                .claim("userId", userDTO.getUserId())
                .setIssuedAt(new Date(now))
                .setExpiration(refreshTokenExpiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("AccessToken Expiry: {}", accessTokenExpiry);
        log.info("RefreshToken Expiry: {}", refreshTokenExpiry);

        return JwtToken.builder()
                .userId(userDTO.getUserId())
                .id(userDTO.getId())
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //현재 이 메서드를 안쓰는 듯
    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String jti = UUID.randomUUID().toString(); // 고유한 JWT ID 생성

        long now = (new Date()).getTime();
        Date validity = new Date(now + 86400000);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .claim("jti", jti)
                .claim("userId",authentication.getPrincipal()) //마이페이지를 위해 추가
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        //Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        Object authClaim = claims.get("auth");
        String userId = (String)claims.get("userId");

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        if (claims.get("userId") == null) {
            throw new RuntimeException("유저 id 정보가 없습니다.");
        }

        // 클레임에서 권한 정보 가져오기
        //Collection<? extends GrantedAuthority> authorities =
        Collection<GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());


        //UserDetails 객체를 만들어서 Authentication 반환
        //UserDetails: interface, User: UserDetails를 구현한 class

        //UserDetails principal = new User(claims.getSubject(), "", authorities);

        Long id = Long.valueOf(claims.getId());

        UserDetails principal = new UserDetailsImpl(claims.getSubject(), id, authorities, userId); // userId 추가
        //return new UsernamePasswordAuthenticationToken(principal, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
    }

    //토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } /*catch (io.jsonwebtoken.security.SignatureException e) { //SignatureException 예외 처리
            log.info("SignatureExpcetion 발생");
        }*/ catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        } catch (Exception e) {
            log.info("asdfadsf");
        }
        return false;
    }

    // refreshToken을 통해 새로운 accessToken을 발급하는 메서드
    public JwtToken refreshAccessToken(String refreshToken) {
        try {
            // refreshToken이 유효한지 검사
            if (!validateToken(refreshToken)) {
                throw new IllegalArgumentException("Refresh Token이 유효하지 않습니다.");
            }

            // refreshToken에서 사용자 정보 추출
            Claims claims = parseClaims(refreshToken);
            String userId = claims.get("userId", String.class);

            // 사용자 정보 기반으로 새로운 accessToken 생성
            UserDTO userDTO = getUserById(userId); // userDTO는 사용자 정보를 담고 있는 DTO

            Authentication authentication = new UsernamePasswordAuthenticationToken(userDTO, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            return generateToken(authentication, userDTO);  // 새로운 accessToken과 refreshToken을 발급하여 반환
        } catch (Exception e) {
            log.error("Refresh Token 처리 중 오류 발생", e);
            throw new IllegalArgumentException("Refresh Token을 처리하는 중 오류가 발생했습니다.");
        }
    }

    // UserDTO로 사용자 정보를 가져오는 메서드 (예시)
    public UserDTO getUserById(String userId) {
        return userRepository.findByUserId(userId)
                .map(user -> new UserDTO(user.getId(), user.getUserId(), user.getRole()))
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // accessToken
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public long getExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Date expiration = claims.getExpiration();
        return expiration.getTime();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", String.class); // 보통 userId나 email이 들어감
    }

    public long getRemainingTime(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().getTime();
    }
}
