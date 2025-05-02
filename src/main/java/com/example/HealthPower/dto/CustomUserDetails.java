package com.example.HealthPower.dto;

import com.example.HealthPower.dto.user.UserDTO;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class CustomUserDetails implements UserDetails {

    //스프링 시큐리티를 구현하는데 중요한 클래스이기 때문에 선언해주었습니다.
    //스프링 시큐리티의 버전이 변경될 때마다 클래스의 버전도 변경될 수 있기 때문입니다.
    private static final long serialVersionUID = 1L;

    private String userId;
    private String password;

    // 인증된 사용자의 권한 정보가 저장될 필드
    private List<GrantedAuthority> userinfoAuthList;

    public CustomUserDetails(UserDTO userDTO) {
        this.userId = userDTO.getUserId();
        this.password = userDTO.getPassword();

        // 사용자의 권한을 GrantedAuthority 객체로 생성하여 저장
        this.userinfoAuthList= new ArrayList<GrantedAuthority>();

        for (AuthorityDTO auth : userDTO.getSecurityAuthList()) {
            userinfoAuthList.add(new SimpleGrantedAuthority(auth.getAuthority()));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userinfoAuthList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId;
    }
}
