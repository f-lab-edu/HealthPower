package com.example.HealthPower.loginUser;

import com.example.HealthPower.dto.UserDTO;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.jwt.JwtAuthenticationFilter;
import com.example.HealthPower.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoginUser.class) != null &&
                parameter.getParameterType().equals(UserDTO.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        //소스 수정
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            return new UserDTO(userDetails); // UserDTO로 매핑 필요
        }

        return null;

        /*//이전 소스
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = jwtAuthenticationFilter.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            return jwtTokenProvider.getUserById(jwtTokenProvider.getUserIdFromToken(token));
        }
        return null;*/
    }
}
