package com.example.HealthPower.impl;

import com.example.HealthPower.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;


@Getter
//UserDetails => Spring Security에서 인증 및 권한 부여에 필요한 사용자 정보를 제공하는 인터페이스
//구현 시 인증과 권한 검사를 수행
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    //임의로 설정
    private static final long serialVersionUID = 174726374856727L;

    private String id;	// DB에서 PK 값
    private String userId;		// 로그인용 ID 값
    private String password;	// 비밀번호
    private String nickname;	//닉네임
    private String email;	//이메일
    private boolean emailVerified;	//이메일 인증 여부
    private boolean locked;	//계정 잠김 여부
    private Collection<GrantedAuthority> authorities;	//권한 목록

    //해당 유저의 권한 목록
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    //비밀번호
    @Override
    public String getPassword() {
        return password;
    }

    //pk값
    @Override
    public String getUsername() {
        return id;
    }

    //계정의 만료 여부 리턴
    //true : 만료
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    //계정의 잠김 여부 리턴
    //true : 잠김
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    //비밀번호 만료 여부 리턴
    //true : 만료
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    //계정의 활성화 여부 리턴
    //true : 활성화
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
