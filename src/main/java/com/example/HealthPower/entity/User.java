package com.example.HealthPower.entity;

import com.example.HealthPower.userType.Gender;
import com.example.HealthPower.userType.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
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

/*    @Transient
    @Column(name = "photo")
    private MultipartFile photo;*/

    @Column(name = "photo_path")
    private String photoPath;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birth")
    private LocalDate birth;

    @Column(name = "authorities")
    private Collection<GrantedAuthority> authorities;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    /*@Column(name = "updatedAt")
    private LocalDateTime updatedAt;
    */

    //Ordinal을 사용 X
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "activated")
    private boolean activated;

   /* @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;*/

    @Override
    //public Collection<? extends GrantedAuthority> getAuthorities() {
    //JoinDTO getAuthorities() 오류로 타입 변경??
    public Collection<GrantedAuthority> getAuthorities() {
        if (role != null) {
            return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
        }
        return Collections.emptyList(); // role이 null일 경우 빈 리스트 반환
    }

}
