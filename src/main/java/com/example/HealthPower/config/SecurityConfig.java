package com.example.HealthPower.config;

import com.example.HealthPower.jwt.JwtAccessDeniedHandler;
import com.example.HealthPower.jwt.JwtAuthenticationEntryPoint;
import com.example.HealthPower.jwt.JwtAuthenticationFilter;
import com.example.HealthPower.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CorsFilter corsFilter;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    //BcryptPasswordEncoder는 위의 PasswordEncoder의 구현 클래스이며, Bcrypt 해시 함수를 사용해 비밀번호를 암호화한다.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
        //csrf, cors, basichttp 비활성화
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                // permit, authenticated 경로 설정
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        // 지정한 경로는 인증 없이 접근 허용
                        .requestMatchers("/", "/members/login", "/members/join", "/test").permitAll()
                        //나머지 모든 경로는 인증 필요
                        .anyRequest().authenticated()
                )
        //세션 관리 구성(세션을 사용하지 않기 때문에 STATELESS로 설정)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                //로그아웃
                //로그아웃 실행은 기본적으로 POST /logout으로만 가능하며
                // csrf 기능을 비활성화 할 경우 또는 RequestMatcher 를 사용할 경우 GET, PUT, DELETE 모두 가능
                .logout(logout->logout
                        .logoutUrl("/members/logout")
                        .logoutSuccessUrl("/")
                        .logoutSuccessHandler(new LogoutSuccessHandler(){
                            @Override
                            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                response.sendRedirect("/"); // 이렇게 되면 위에서 설정한 logoutSuccessUrl은 적용되지 않는다.
                            }
                        })
                        //로그아웃 핸들러 추가(세션 무효화 처리) -> jwt방식에 굳이 필요한가?
                                .addLogoutHandler(((request, response, authentication) ->
                                {
                                    HttpSession session = request.getSession();
                                    session.invalidate();
                                }))
                                //로그아웃 성공 핸들러 추가(리다이렉션 처리) logout을 성공하게 되면 실행되는 핸들러이다.
                                //위에서는 로그아웃 시 /logoutSuccess로 리다이렉트 하도록 설정이 했다.
                                //(주의할점은 로그아웃을 할 경우 인증 상태가 사라지기 때문에 http.anyRequest().authenticaiton() 설정으로 인해 페이지 이동이 정상적을 안될 수 있다.
                                //그래서 http.requestMatcher("logoutSuccess").permitAll() 설정을 따로 해주었다.)
                                .logoutSuccessHandler(((request, response, authentication) ->
                                {

                                    response.sendRedirect("/");
                                }))
                                .deleteCookies("JSESSIONID","access_token")
                        // 로그아웃 시 SecurityContextLogoutHandler가 인증객체(Authentication)을 삭제한다.
                        .clearAuthentication(true)
                        .addLogoutHandler(new LogoutHandler() {
                            @Override
                            public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                                HttpSession session = request.getSession();
                                session.invalidate();
                                SecurityContextHolder.getContextHolderStrategy().getContext().setAuthentication(null);  // security context에 저장된 authentication 객체를 없앤다.
                                SecurityContextHolder.getContextHolderStrategy().clearContext();    // security context 초기화를 한다.
                            }
                        }));

                //.with(new JwtConfig(jwtTokenProvider), customizer -> {});

                //.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
