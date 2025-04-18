package com.example.HealthPower.jwt;

import com.example.HealthPower.dto.JoinDTO;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
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

        System.out.println("generateToken method called");
        if (authentication == null) {
            System.out.println("Authentication object is null");
        } else {
            System.out.println("Authentication: " + authentication.getName());
        }

        //권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresln = new Date(now + 86400000);

        System.out.println("Key used to sign the token: " + Arrays.toString(key.getEncoded()));  // key 출력

        //accessToken을 통해 jwt토큰을 복호화하기 때문에 여기서 내가 원하는 정보를 설정
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                //.claim("auth", authorities)
                //테스트용
                .claim("auth", "test_admin")
                .claim("userId", userDTO.getUserId())
                .claim("id", userDTO.getId())
                .signWith(key, SignatureAlgorithm.HS256) //key 값이 서버에서 검증하는 key 값과 동일해야 함.
                .setExpiration(accessTokenExpiresln)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        //id설정을 어떻게 해줘야하지?
        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

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
        System.out.println("first_auth claim: " + authClaim + ", userId : " + userId);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        if (claims.get("userId") == null) {
            throw new RuntimeException("유저 id 정보가 없습니다.");
        }

        System.out.println("second_auth claim: " + authClaim + ", userId : " + userId);

        // 클레임에서 권한 정보 가져오기
        //Collection<? extends GrantedAuthority> authorities =
        Collection<GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());


        //UserDetails 객체를 만들어서 Authentication 반환
        //UserDetails: interface, User: UserDetails를 구현한 class

        //UserDetails principal = new User(claims.getSubject(), "", authorities);

        UserDetails principal = new UserDetailsImpl(claims.getSubject(), "", authorities, userId); // userId 추가
        //return new UsernamePasswordAuthenticationToken(principal, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
    }

    //토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            System.out.println("Key used to validate the token: " + Arrays.toString(key.getEncoded()));  // key 출력

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
}
