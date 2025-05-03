package com.example.HealthPower.impl;

import com.example.HealthPower.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;


@Getter
//UserDetails => Spring Security에서 인증 및 권한 부여에 필요한 사용자 정보를 제공하는 인터페이스
//구현 시 인증과 권한 검사를 수행
public class UserDetailsImpl implements UserDetails {

    private final User user;

    // 기존 생성자 (User 객체만 받는 경우)
    public UserDetailsImpl(User user) {
        this.user = user;
        this.userId = user.getUserId();  // User 객체에서 userId를 추출
        this.authorities = new ArrayList<>(user.getAuthorities());  // User 객체에서 권한을 추출
    }

    // userId와 authorities를 받는 생성자 추가
    public UserDetailsImpl(String subject, String id, Collection<GrantedAuthority> authorities, String userId) {
        this.user = null;  // 실제 User 객체를 저장하지 않음
        this.username = subject;
        this.userId = userId;  // 외부에서 전달된 userId 설정
        this.authorities = authorities; // 외부에서 전달된 권한 설정
        this.id = id;
    }

    //임의로 설정
    private static final long serialVersionUID = 174726374856727L;

    private String id;	// DB에서 PK 값
    private String userId;		// 로그인용 ID 값
    private String password;    // 비밀번호
    private String username; // 유저 이름
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
    /*public String getUsername() {
        return id;
    }*/
    public String getUsername() {
        return userId;
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
