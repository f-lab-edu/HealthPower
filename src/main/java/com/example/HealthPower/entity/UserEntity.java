package com.example.HealthPower.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Column(name = "userId", unique = true, nullable = false)
    private String userId;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nickName", unique = true, nullable = false)
    private String nickName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "birth")
    private String birth;

    @Column(name = "photo")
    private String photo;

    @Column(name = "authorities")
    //private String role;
    private Collection<GrantedAuthority> authorities;	//권한 목록

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
