package com.example.HealthPower.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

//처음에 정한 secret-key와 만료시간을 이용해서 jwtToken을 생성하는 함수와 jwtToken의 유효성을 검증하는 함수를 정의하는 클래스
/*public class JwtTokenProvider {

    *//*public String createToken(String userPk, List<String> roles) {
        //권한 가져오기
        Claims claims = Jwts.claims().setSubject(userPk);  // JWT payload 에 저장되는 정보단위
        claims.put("roles", roles); //정보는 key/value 형태로 저장됨.
        Date now = new Date();
        //Access Token 생성
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKet)
                .compact();

        return accessToken;
    }*//*

    // JWT 토큰에서 인증 정보 조회
    *//*public Authentication getAuthentication(String token) {
        Claims claims = parseJwt(token);
        String s = claims.getSubject();

        UserDetails userDetails = customUserDetailService.loadUserByUsername(s);
        return new UsernamePasswordAuthenticationToken(userDetails, "".userDetails.getAuthorities());
    }*//*

    // 토큰 정보를 검증하는 메서드
    *//*public boolean validateToken(String token) {
        Claims claims = null;
        try {
            claims = parseJwt(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }*//*

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken)) {
            return bearerToken;
        }
        return null;
    }

    *//*public Claims parseJwt(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();

        return claims;
    }*//*
}*/
