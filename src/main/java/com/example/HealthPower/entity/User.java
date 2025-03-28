package com.example.HealthPower.entity;

import com.example.HealthPower.userType.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "userId", unique = true, nullable = false)
    private String userId;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "nickname", unique = true, nullable = false)
    private String nickname;

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

    /*@Column(name = "authorities")
    //private String role;
    private Collection<GrantedAuthority> authorities;	//권한 목록*/

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    //Ordinal을 사용 X
    @Enumerated(EnumType.STRING)
    private Role Role;

    @Column(name = "activated")
    private boolean activated;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

    /*@Override
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
    }*/
}
