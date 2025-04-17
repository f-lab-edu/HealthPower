package com.example.HealthPower.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;


@Data
@AllArgsConstructor
@Builder //@Builder를 상위에 두면 제대로 build가 안될 수 있으니 class명 바로 위에 넣어주는 것을 권장.
public class JwtToken {

    private Long id;
    private String userId;
    private String username;
    private String grantType; //JWT에 대한 인증 타입이다.(Bearer 인증 방식 사용) + Access Token을 HTTP 요청의 Authorization 헤더에 포함하여 전송
    private String accessToken; // 인증된 사용자가 특정 리소스에 접근할 때 사용되는 토큰
    private String refreshToken; //accessToken (만료 시) 갱신 토큰
}
